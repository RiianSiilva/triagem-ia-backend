package br.edu.facul.TriagemIA.dto;
import br.edu.facul.TriagemIA.model.TriagemRequest;

public record TriagemManualRequest(
        TriagemRequest dadosTriagem,
        String corEscolhida,
        String justificativaClinica,
        String condutaRecomendada,
        String nomeProfissional
) {
}
