#!/usr/bin/env python3
"""Non-destructive smoke test for RuoYi + Warm-Flow Jimmer/PostgreSQL.

Covers health, captcha, login, getInfo, getRouters, and representative
system/monitor/tool/flow list APIs.
If Redis is reachable, the script reads the captcha answer by uuid and performs login automatically.
"""
from __future__ import annotations

import argparse
import base64
import json
import os
import subprocess
import sys
import time
import urllib.error
import urllib.parse
import urllib.request
from typing import Any, Dict, Iterable, Optional, Tuple

DEFAULT_BASE_URL = "http://192.168.2.226:18080/"
DEFAULT_USERNAME = "admin"
DEFAULT_PASSWORD = "admin123"
CAPTCHA_KEY_PREFIX = "captcha_codes:"


class SmokeError(RuntimeError):
    pass


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Smoke test RuoYi Warm-Flow deployment")
    parser.add_argument("--base-url", default=os.getenv("SMOKE_BASE_URL", DEFAULT_BASE_URL))
    parser.add_argument("--username", default=os.getenv("SMOKE_USERNAME", DEFAULT_USERNAME))
    parser.add_argument("--password", default=os.getenv("SMOKE_PASSWORD", DEFAULT_PASSWORD))
    parser.add_argument("--redis-host", default=os.getenv("REDIS_HOST", "192.168.2.226"))
    parser.add_argument("--redis-port", type=int, default=int(os.getenv("REDIS_PORT", "6379")))
    parser.add_argument("--redis-db", type=int, default=int(os.getenv("REDIS_DATABASE", "0")))
    parser.add_argument("--redis-password", default=os.getenv("REDIS_PASSWORD", ""))
    parser.add_argument(
        "--redis-container",
        default=os.getenv("REDIS_CONTAINER", ""),
        help="Optional Docker container name whose redis-cli can read captcha keys",
    )
    parser.add_argument("--timeout", type=float, default=float(os.getenv("SMOKE_TIMEOUT", "10")))
    parser.add_argument("--skip-login", action="store_true", help="Only test anonymous endpoints")
    return parser.parse_args()


def normalize_base_url(value: str) -> str:
    return value.rstrip("/") + "/"


def request_json(method: str, base_url: str, path: str, *, token: Optional[str] = None,
                 payload: Optional[Dict[str, Any]] = None, timeout: float = 10) -> Tuple[int, Dict[str, Any]]:
    url = urllib.parse.urljoin(base_url, path.lstrip("/"))
    data = None
    headers = {"Accept": "application/json"}
    if payload is not None:
        data = json.dumps(payload).encode("utf-8")
        headers["Content-Type"] = "application/json;charset=UTF-8"
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            raw = resp.read().decode("utf-8", errors="replace")
            body = json.loads(raw) if raw else {}
            return resp.status, body
    except urllib.error.HTTPError as exc:
        raw = exc.read().decode("utf-8", errors="replace")
        try:
            body = json.loads(raw) if raw else {}
        except json.JSONDecodeError:
            body = {"raw": raw}
        return exc.code, body
    except (urllib.error.URLError, TimeoutError) as exc:
        raise SmokeError(f"{method} {url} failed: {exc}") from exc



def request_text(method: str, base_url: str, path: str, *, token: Optional[str] = None,
                 timeout: float = 10) -> Tuple[int, str]:
    url = urllib.parse.urljoin(base_url, path.lstrip("/"))
    headers = {"Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"}
    if token:
        headers["Authorization"] = "Bearer " + token
    req = urllib.request.Request(url, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=timeout) as resp:
            raw = resp.read().decode("utf-8", errors="replace")
            return resp.status, raw
    except urllib.error.HTTPError as exc:
        raw = exc.read().decode("utf-8", errors="replace")
        return exc.code, raw
    except (urllib.error.URLError, TimeoutError) as exc:
        raise SmokeError(f"{method} {url} failed: {exc}") from exc

def require_success(name: str, status: int, body: Dict[str, Any], allow_codes: Iterable[int] = (200,)) -> None:
    if status not in allow_codes:
        raise SmokeError(f"{name} HTTP {status}: {body}")
    code = body.get("code")
    if code not in (None, 0, 200):
        raise SmokeError(f"{name} business code {code}: {body}")


def normalize_redis_value(value: Optional[str]) -> Optional[str]:
    """Return the plain captcha code stored by RuoYi's JSON Redis serializer.

    RuoYi writes captcha values through the application RedisTemplate. Depending
    on the client used for smoke testing, Redis can return either ``1234`` or a
    JSON encoded string such as ``"1234"``. Login expects the plain code, so the
    smoke test normalizes both forms before posting to ``/login``.
    """
    if not value:
        return None
    value = str(value).strip()
    if not value:
        return None
    try:
        decoded = json.loads(value)
    except json.JSONDecodeError:
        return value
    return str(decoded).strip() if decoded is not None else None


def redis_get_captcha(args: argparse.Namespace, uuid: str) -> Optional[str]:
    key = CAPTCHA_KEY_PREFIX + uuid
    try:
        import redis  # type: ignore
    except ImportError:
        pass
    else:
        try:
            client = redis.Redis(
                host=args.redis_host,
                port=args.redis_port,
                db=args.redis_db,
                password=args.redis_password or None,
                socket_timeout=args.timeout,
                decode_responses=True,
            )
            value = client.get(key)
            if value:
                return normalize_redis_value(str(value))
        except Exception as exc:  # noqa: BLE001 - fall through to redis-cli
            print(f"WARN python redis captcha read failed: {exc}", file=sys.stderr)

    cmd = [
        "redis-cli",
        "-h", args.redis_host,
        "-p", str(args.redis_port),
        "-n", str(args.redis_db),
    ]
    if args.redis_password:
        cmd.extend(["-a", args.redis_password])
    cmd.extend(["--raw", "GET", key])
    value = None
    try:
        proc = subprocess.run(cmd, check=False, capture_output=True, text=True, timeout=args.timeout)
    except (FileNotFoundError, subprocess.TimeoutExpired) as exc:
        print(f"WARN redis-cli captcha read unavailable: {exc}", file=sys.stderr)
    else:
        if proc.returncode != 0:
            print(f"WARN redis-cli captcha read failed: {proc.stderr.strip()}", file=sys.stderr)
        else:
            value = normalize_redis_value(proc.stdout)
    if value:
        return value

    if not args.redis_container:
        return None
    docker_cmd = [
        "docker",
        "exec",
        args.redis_container,
        "redis-cli",
        "-n", str(args.redis_db),
    ]
    if args.redis_password:
        docker_cmd.extend(["-a", args.redis_password])
    docker_cmd.extend(["--raw", "GET", key])
    try:
        proc = subprocess.run(docker_cmd, check=False, capture_output=True, text=True, timeout=args.timeout)
    except (FileNotFoundError, subprocess.TimeoutExpired) as exc:
        print(f"WARN docker redis-cli captcha read unavailable: {exc}", file=sys.stderr)
        return None
    if proc.returncode != 0:
        print(f"WARN docker redis-cli captcha read failed: {proc.stderr.strip()}", file=sys.stderr)
        return None
    return normalize_redis_value(proc.stdout)


def smoke(args: argparse.Namespace) -> int:
    base_url = normalize_base_url(args.base_url)
    print(f"SMOKE base_url={base_url} username={args.username}")

    status, body = request_json("GET", base_url, "/health", timeout=args.timeout)
    require_success("health", status, body)
    print("OK health")

    status, body = request_text("GET", base_url, "/warm-flow-ui/index.html", timeout=args.timeout)
    if status != 200 or "Warm-Flow" not in body:
        raise SmokeError("workflow designer HTML unavailable")
    print("OK warm-flow-ui.index")

    status, captcha = request_json("GET", base_url, "/captchaImage", timeout=args.timeout)
    require_success("captcha", status, captcha)
    uuid = str(captcha.get("uuid") or "")
    if captcha.get("img"):
        base64.b64decode(str(captcha["img"]), validate=False)
    print(f"OK captcha captchaEnabled={captcha.get('captchaEnabled')} uuid={uuid or '-'}")

    if args.skip_login:
        print("SKIP login/authenticated APIs (--skip-login)")
        return 0

    code = ""
    if captcha.get("captchaEnabled") is False:
        print("INFO captcha disabled; login without captcha code")
    elif uuid:
        code = redis_get_captcha(args, uuid) or ""
        if not code:
            raise SmokeError(
                "captcha is enabled but answer was not readable from Redis; "
                "install redis Python package, set REDIS_HOST/REDIS_PASSWORD, "
                "set --redis-container, or use --skip-login"
            )
    else:
        raise SmokeError("captcha enabled but uuid missing")

    payload = {"username": args.username, "password": args.password, "code": code, "uuid": uuid}
    status, login = request_json("POST", base_url, "/login", payload=payload, timeout=args.timeout)
    require_success("login", status, login)
    token = login.get("token")
    if not token:
        raise SmokeError(f"login response missing token: {login}")
    print("OK login")

    checks = [
        ("getInfo", "GET", "/getInfo"),
        ("getRouters", "GET", "/getRouters"),
        ("system.user.list", "GET", "/system/user/list?pageNum=1&pageSize=1"),
        ("system.role.list", "GET", "/system/role/list?pageNum=1&pageSize=1"),
        ("system.menu.list", "GET", "/system/menu/list"),
        ("system.dept.list", "GET", "/system/dept/list"),
        ("system.post.list", "GET", "/system/post/list?pageNum=1&pageSize=1"),
        ("system.dict.type.list", "GET", "/system/dict/type/list?pageNum=1&pageSize=1"),
        ("system.dict.data.list", "GET", "/system/dict/data/list?pageNum=1&pageSize=1"),
        ("system.config.list", "GET", "/system/config/list?pageNum=1&pageSize=1"),
        ("monitor.server", "GET", "/monitor/server"),
        ("monitor.cache", "GET", "/monitor/cache"),
        ("monitor.operlog.list", "GET", "/monitor/operlog/list?pageNum=1&pageSize=1"),
        ("monitor.logininfor.list", "GET", "/monitor/logininfor/list?pageNum=1&pageSize=1"),
        ("monitor.job.list", "GET", "/monitor/job/list?pageNum=1&pageSize=1"),
        ("tool.gen.list", "GET", "/tool/gen/list?pageNum=1&pageSize=1"),
        ("flow.definition.list", "GET", "/flow/definition/list?pageNum=1&pageSize=1"),
        ("flow.form.list", "GET", "/flow/form/list?pageNum=1&pageSize=1"),
        ("flow.todo.page", "GET", "/flow/execute/toDoPage?pageNum=1&pageSize=1"),
        ("flow.done.page", "GET", "/flow/execute/donePage?pageNum=1&pageSize=1"),
        ("warm-flow.query-def", "GET", "/warm-flow/query-def"),
        ("warm-flow.listener-list", "GET", "/warm-flow/listener-list"),
    ]
    for name, method, path in checks:
        status, body = request_json(method, base_url, path, token=token, timeout=args.timeout)
        require_success(name, status, body)
        print(f"OK {name}")

    print("SMOKE PASS")
    return 0


def main() -> int:
    try:
        return smoke(parse_args())
    except SmokeError as exc:
        print(f"SMOKE FAIL: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
