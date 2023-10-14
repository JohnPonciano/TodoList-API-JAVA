package br.com.johnprofile.vercel.TodoList.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.johnprofile.vercel.TodoList.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("checando existencia do usuario");
        var servletPath = request.getServletPath();

        if(servletPath.startsWith("/tasks/")){

            // pegar usuario e senha

            var authorization = request.getHeader("Authorization");
            var auth_encoded = authorization.substring("Basic".length()).trim();

            byte[] auth_decoded = Base64.getDecoder().decode(auth_encoded);
            var auth_string = new String(auth_decoded);
            String[] credentials = auth_string.split(":");
            String username = credentials[0];
            String password = credentials[1];

            System.out.println("Authorization recebida");
            System.out.println(auth_encoded);
            System.out.println(username);
            System.out.println(password);
            //valirdar usuario
            var user = this.userRepository.findByUsername(username);
            if(user == null){
                response.sendError(401, "Usuario sem autorizacao");
            }
            else {
                //validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified){
                    request.setAttribute("idUser",user.getId());
                    filterChain.doFilter(request,response);
                }else{
                    response.sendError(401, "Usuario sem autorizacao");
                }

            }
        }
        else {
            filterChain.doFilter(request,response);
        }

    }
}
