package MongoDB.entities;

import lombok.*;

// Objeto associado à tabela experiencia, vai ser um recurso partilhado
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Experiencia {
   private String dataHora;
   private String id;
}
