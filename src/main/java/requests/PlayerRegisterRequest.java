package requests;

import model.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRegisterRequest {
    String username;
    String password_change;
    String password_repeat;
    String email;
    String name;
    String surname;
    Currency currency_code;
}
