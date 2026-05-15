# RuoYi + Warm-Flow Jimmer/PostgreSQL 部署与运维说明

本文面向 `warm-flow-jimmer-demo` 后台的开发/演示环境。默认复用 `192.168.2.226` 上 `dev-postgres` 与 `dev-redis`，不在部署脚本中删除或重建已有数据。

## 默认访问

- Web/API: <http://192.168.2.226:18080/>
- Health: <http://192.168.2.226:18080/health>
- 默认账号: `admin/admin123`
- 默认后端端口: `18080`

## 环境变量

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `SERVER_PORT` | `18080` | 宿主机访问端口；容器内固定映射到 `18080`。 |
| `SPRING_PROFILES_ACTIVE` | `druid` | Spring profile，加载 `application-druid.yml`。 |
| `RUOYI_PROFILE` | `/home/ruoyi/uploadPath` | 上传文件目录，compose 会挂载为持久化卷。 |
| `WARM_FLOW_DB_URL` | `jdbc:postgresql://dev-postgres:5432/warm_flow_jimmer_demo` | PostgreSQL JDBC URL。 |
| `WARM_FLOW_DB_USERNAME` | `warm_flow_jimmer_demo` | PostgreSQL 用户。 |
| `WARM_FLOW_DB_PASSWORD` | 空 | PostgreSQL 密码，按目标环境设置。 |
| `REDIS_HOST` | `dev-redis` | Redis 主机。 |
| `REDIS_PORT` | `6379` | Redis 端口。 |
| `REDIS_DATABASE` | `0` | Redis DB。 |
| `REDIS_PASSWORD` | 空 | Redis 密码，按目标环境设置。 |
| Docker network | `dev-infra` | 应用、`dev-postgres`、`dev-redis` 共享的外部 Docker 网络。 |
| `RUOYI_TOKEN_SECRET` | `warm-flow-jimmer-postgres-change-me` | JWT 密钥，非本地环境必须覆盖。 |
| `RUOYI_TOKEN_EXPIRE_MINUTES` | `120` | token 有效期。 |
| `JIMMER_SHOW_SQL` | `false` | 是否输出 Jimmer SQL。 |
| `JIMMER_PRETTY_SQL` | `false` | 是否格式化 SQL。 |

建议在服务器上创建 `.env`，不要把真实密码提交进 Git：

```sh
SERVER_PORT=18080
SPRING_PROFILES_ACTIVE=druid
WARM_FLOW_DB_URL=jdbc:postgresql://dev-postgres:5432/warm_flow_jimmer_demo
WARM_FLOW_DB_USERNAME=warm_flow_jimmer_demo
WARM_FLOW_DB_PASSWORD=change-me
REDIS_HOST=dev-redis
REDIS_PORT=6379
REDIS_DATABASE=0
REDIS_PASSWORD=change-me-if-any
RUOYI_TOKEN_SECRET=change-me-long-random-secret
```

## 初始化数据库

初始化 SQL 已生成在：

- `sql/postgresql/ruoyi-warm-flow-jimmer-postgres.sql`

首次部署前在 PostgreSQL 所在主机或可访问 PostgreSQL 的机器执行。示例命令会创建/授权演示库用户并导入 RuoYi、Quartz、Warm-Flow 与示例菜单数据；`ruoyi-warm-flow-jimmer-postgres.sql` 默认只允许空库/空 `public` schema 初始化，检测到已有表会拒绝继续。确需重置演示库时，必须先备份、人工审阅 SQL，并显式传入 `-v allow_destructive_reset=true`。

```sh
# 1) 在维护库创建/确认应用库和用户；app_password 按目标环境替换
psql "postgresql://postgres@192.168.2.226:5432/postgres" \
  -v ON_ERROR_STOP=1 \
  -v app_password='replace-with-strong-password' \
  -f sql/postgresql/00-create-database.sql

# 可选：创建一次性验收库时覆盖库名/用户名，避免影响共享演示库
psql "postgresql://postgres@192.168.2.226:5432/postgres" \
  -v ON_ERROR_STOP=1 \
  -v app_db='warm_flow_jimmer_smoke' \
  -v app_user='warm_flow_jimmer_smoke' \
  -v app_password='replace-with-strong-password' \
  -f sql/postgresql/00-create-database.sql

# 2) 连接应用库导入完整 RuoYi + Quartz + Warm-Flow + 示例数据
psql "postgresql://warm_flow_jimmer_demo@192.168.2.226:5432/warm_flow_jimmer_demo" \
  -v ON_ERROR_STOP=1 \
  -f sql/postgresql/ruoyi-warm-flow-jimmer-postgres.sql

# 只在已备份且确认要重置演示库时使用：
psql "postgresql://warm_flow_jimmer_demo@192.168.2.226:5432/warm_flow_jimmer_demo" \
  -v ON_ERROR_STOP=1 \
  -v allow_destructive_reset=true \
  -f sql/postgresql/ruoyi-warm-flow-jimmer-postgres.sql
```

如果需要重新生成初始化 SQL：

```sh
python3 scripts/generate_pg_init.py
```

## 构建与容器部署

在项目根目录执行：

```sh
mvn -DskipTests clean package
docker network inspect dev-infra >/dev/null 2>&1 || docker network create dev-infra
docker compose -f docker-compose.deploy.yml up -d --build
docker compose -f docker-compose.deploy.yml ps
docker logs -f warm-flow-jimmer-demo
```

也可以使用包装脚本：

```sh
bin/deploy_docker.sh
```

重启/停止：

```sh
docker compose -f docker-compose.deploy.yml restart warm-flow-jimmer-demo
docker compose -f docker-compose.deploy.yml stop warm-flow-jimmer-demo
docker compose -f docker-compose.deploy.yml up -d warm-flow-jimmer-demo
```

只查看状态（非破坏性）：

```sh
docker ps --filter name=warm-flow-jimmer-demo
curl -fsS http://192.168.2.226:18080/health
```

## 烟测

烟测脚本：`scripts/smoke_remote.py`，覆盖：

- `/health`
- `/warm-flow-ui/index.html`
- `/captchaImage`
- `/login`
- `/getInfo`
- `/getRouters`
- `/system/user/list`
- `/system/role/list`
- `/system/menu/list`
- `/system/dept/list`
- `/system/post/list`
- `/system/dict/type/list`
- `/system/dict/data/list`
- `/system/config/list`
- `/monitor/server`
- `/monitor/cache`
- `/monitor/operlog/list`
- `/monitor/logininfor/list`
- `/monitor/job/list`
- `/tool/gen/list`
- `/flow/definition/list`
- `/flow/form/list`
- `/flow/execute/toDoPage`
- `/flow/execute/donePage`

验证码开启时，脚本会用 `/captchaImage` 返回的 `uuid` 读取 Redis key `captcha_codes:{uuid}`，自动拿到验证码并登录。脚本优先使用 Python `redis` 包，缺失时回退到 `redis-cli`；两者都不可用或 Redis 不可达时，可使用 `--skip-login` 仅验证匿名接口。
在部署机只提供 Redis Docker 容器、宿主机没有 `redis-cli` 时，可增加 `--redis-container dev-redis`，脚本会通过 `docker exec dev-redis redis-cli ...` 读取验证码。

```sh
python3 scripts/smoke_remote.py \
  --base-url http://192.168.2.226:18080/ \
  --username admin \
  --password admin123 \
  --redis-host dev-redis \
  --redis-port 6379 \
  --redis-container dev-redis

# 或
scripts/smoke_remote.sh --base-url http://192.168.2.226:18080/
```

## 故障排查

### `/health` 不通

1. `docker ps --filter name=warm-flow-jimmer-demo` 确认容器是否运行。
2. `docker logs --tail=200 warm-flow-jimmer-demo` 查看启动异常。
3. 确认端口映射：`docker compose -f docker-compose.deploy.yml ps`。
4. 确认服务器防火墙或安全组允许访问 `18080`。

### 数据库连接失败

1. 核对 `WARM_FLOW_DB_URL/WARM_FLOW_DB_USERNAME/WARM_FLOW_DB_PASSWORD`。
2. 从应用所在主机执行只读连通性检查：`psql "$WARM_FLOW_DB_URL" -c 'select 1'`（JDBC URL 需要换成 psql URL）。
3. 查看 PostgreSQL 是否允许来自容器/宿主机的连接，以及数据库、用户、schema 权限是否已初始化。

### Redis 或验证码登录失败

1. 核对 `REDIS_HOST/REDIS_PORT/REDIS_DATABASE/REDIS_PASSWORD`。
2. 用 `redis-cli -h 192.168.2.226 -p 6379 ping` 检查连通性。
3. 调用 `/captchaImage` 后检查 Redis 是否出现 `captcha_codes:*` key。
4. 烟测机未安装 Python `redis` 包且没有 `redis-cli` 时，先安装任一工具或使用 `--skip-login` 跳过登录类接口。

### 登录失败或账号不可用

1. 确认初始化 SQL 已导入，默认账号为 `admin/admin123`。
2. 如果连续输错导致账号锁定，等待 `user.password.lockTime` 或清理对应 Redis 登录失败 key。
3. 检查系统时间和 token 配置，尤其是 `RUOYI_TOKEN_SECRET` 与 `RUOYI_TOKEN_EXPIRE_MINUTES`。

### 流程列表为空或接口异常

1. 确认初始化 SQL 包含 Warm-Flow 表及示例流程定义。
2. 检查 `/flow/definition/list`、`/flow/form/list` 的响应 code/msg。
3. 打开 `JIMMER_SHOW_SQL=true` 临时查看 SQL，定位字段、表名或权限问题；排查后关闭。
