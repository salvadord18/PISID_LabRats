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
   private Corredor[] corredores;

   public void addCorredor(Corredor corredor){
      for(int i = 0; i < corredores.length; i++){
         if(corredores[i] == null)
            corredores[i] = corredor;
      }
   }
}
