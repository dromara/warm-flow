package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    private static final Logger log = LoggerFactory.getLogger(SysLoginController.class);

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RedisCache redisCache;

    @Anonymous
    @GetMapping("/gitee/callback")
    public void giteeCallback(@RequestParam String code, HttpServletResponse res) {
        try {
            String tokenUrl = "https://gitee.com/oauth/token";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", "e4d91a68aa97c2b448906abcfcfbc1b09e1f3534ca4c4126442da1819c916511");
            params.add("client_secret", "885661ead47735120985d9e10931360685e4393ef147a90de7df1a3dd6f21b14");
            params.add("code", code);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", "http://www.warm-flow.cn/prod-api/gitee/callback");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            log.info("code:{}", code);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            log.info("response:{}", response);
            log.info("response.getStatusCode():{}", response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, String> tokenMap = (Map<String, String>) response.getBody();
                String accessToken = tokenMap.get("access_token");
                String giteeStarId = IdUtils.simpleUUID();
                redisCache.setCacheObject("gitee_star:" + giteeStarId, accessToken, 12 * 60, TimeUnit.MINUTES);
                log.info("giteeStarId:{}", giteeStarId);
                // 重定向到/login页面
                res.sendRedirect("http://www.warm-flow.cn/login?giteeStarId=" + giteeStarId);
            } else {
                log.info("response.getStatusCode()1:{}", response.getStatusCode());
                res.sendRedirect("http://www.warm-flow.cn/login");
            }
        } catch (Exception e) {
            // 重定向到/login页面
            try {
                log.info("e.getMessage():{}", e.getMessage());
                res.sendRedirect("http://www.warm-flow.cn/login");
            } catch (IOException ex) {
                log.info("ex.getMessage():{}", ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
    }

    @Anonymous
    @GetMapping("/gitee/checkStar/{giteeStarId}")
    public AjaxResult checkStar(@PathVariable("giteeStarId") String giteeStarId) {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("star", false);
        try {
            Object cacheObject = redisCache.getCacheObject("gitee_star:" + giteeStarId);
            if (cacheObject != null) {
                String accessToken = (String) cacheObject;
                if (StringUtils.isNotEmpty(accessToken)) {
                    // 检查用户是否Star了指定仓库
                    String checkUrl = String.format("https://gitee.com/api/v5/user/starred/%s/%s?access_token=%s"
                            , "dromara", "warm-flow", accessToken);

                    ResponseEntity<String> checkResponse = restTemplate.getForEntity(checkUrl, String.class);
                    if (checkResponse.getStatusCode() == HttpStatus.NO_CONTENT) {
                        ajax.put("star", true);
                    } else {
                        ajax.put("star", false);
                    }
                }

            }
        } catch (Exception e) {
            return AjaxResult.error("大佬，检测未Star...").put("redirectUrl", "http://www.warm-flow.cn/login");
        }
        return ajax;
    }

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        ajax.put("isDefaultModifyPwd", initPasswordIsModify(user.getPwdUpdateDate()));
        ajax.put("isPasswordExpired", passwordIsExpiration(user.getPwdUpdateDate()));
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
