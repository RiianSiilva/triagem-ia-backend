package br.edu.facul.TriagemIA.model;

import java.time.LocalDate;
import java.util.List;

// criando uma classe imutavel com record, evitando tbm a necessidade de criar mtodos getter e setter e construtores
public record TriagemRequest(
        // ETAPA 1 — IDENTIFICAÇÃO
        String nomePaciente,
        String cpf,
        LocalDate dataNascimento,
        String sexoBiologico,
        String telefoneContato,
        String nomeResponsavel,

        // ETAPA 2 — QUEIXA PRINCIPAL (Questões para o paciente)
        String queixaPrincipal,
        List<String> sintomas,
        String localDor,
        Integer nivelDor,
        String inicioSintomas,
        String evolucaoSintomas,
        boolean sintomaPioraEsforco,

        // ETAPA 3 — SINAIS VITAIS (medidos ou informados)
        Double temperatura,
        Integer pressaoSistolica,
        Integer pressaoDiastolica,
        Integer saturacaoO2,
        Integer frequenciaCardiaca,
        Integer frequenciaRespiratoria,
        Double peso,
        Double altura,
        String glicemia,

        // ETAPA 4 — HISTÓRICO CLÍNICO
        List<String> doencasPreexistentes,
        List<String> medicamentosEmUso,
        List<String> alergias,
        boolean gestante,
        Integer semanasGestacao,
        boolean jaFoiAtendidoHoje,
        String observacoes
) {
}