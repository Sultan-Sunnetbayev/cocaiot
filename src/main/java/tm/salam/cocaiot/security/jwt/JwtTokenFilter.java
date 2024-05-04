package tm.salam.cocaiot.security.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tm.salam.cocaiot.services.RoleService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RoleService roleService;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, RoleService roleService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleService = roleService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        final String token=jwtTokenProvider.getToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {

                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                if (authentication != null && checkCategoryStatusForRole(request, jwtTokenProvider.getUserRoleName(token))) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }catch (JwtException jwtException){
            jwtException.printStackTrace();
        }
        filterChain.doFilter(request, response);

        return;
    }

    private boolean checkCategoryStatusForRole(final HttpServletRequest request, final String roleName){

        final String requestURI=request.getRequestURI();
        StringBuilder categoryName=new StringBuilder();

        for(int i=8; i<requestURI.length() && requestURI.charAt(i)!='/'; i++){
            categoryName.append(requestURI.charAt(i));
        }
        boolean statusCategoryForRole=true;

        switch (categoryName.toString()){
            case "cocaiot-member":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Участники ТППТ");
                break;
            case "commercial-society":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Хозяйственные общества");
                break;
            case "entrepreneur":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Предприниматели");
                break;
            case "foreign-company":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Иностранные компании");
                break;
            case "localization":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Локализация");
                break;
            case "mailing":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Рассылки");
                break;
            case "organization":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Министерствы");
                break;
            case "personal-enterprise":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Частные предприятия");
                break;
            case "persons":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Физическое лицо");
                break;
            case "setting":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Настройки пользователи");
                break;
            case "state_organization":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Государственные организаци");
                break;
            case "statistics":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Статистика");
                break;
            case "type_activity":
                statusCategoryForRole=roleService.getPrivilageByRoleNameAndCategoryName(roleName, "Вид деятельности");
                break;
        }

        return statusCategoryForRole;
    }

}
