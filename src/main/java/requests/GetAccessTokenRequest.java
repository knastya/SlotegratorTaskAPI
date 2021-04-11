package requests;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAccessTokenRequest {
    String grant_type;
    String scope;
    String username;
    String password;
}
