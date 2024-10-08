package co.idesoft.architetture.mvcservices.controllers.dto;

import co.idesoft.architetture.mvcservices.entities.Cassa;

public record CassaItemDto(
        Long cassaId,
        String nome,
        String descrizione) {

    public static CassaItemDto fromEntity(Cassa cassa) {
        return new CassaItemDto(
                cassa.getCassaId(),
                cassa.getNome(),
                cassa.getDescrizione());
    }
}
