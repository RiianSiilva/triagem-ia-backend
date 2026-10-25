package br.edu.facul.TriagemIA.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

//registra essa interface como um serviço de Inteligência Artificial
@AiService
public interface TriagemAiService { // interface == contrato de regras que uma classe deve seguir (implements)

    //configura o comportamento do modelo
    @SystemMessage("""
        Você é um especialista em triagem hospitalar treinado no Protocolo de Manchester (MTS).
        
        Sua função é classificar o paciente em uma das 5 categorias de urgência:
        
        VERMELHO  - Emergência. Risco imediato de vida. Atendimento imediato (0 min).
        LARANJA   - Muito urgente. Risco de deterioração rápida. Atendimento em 10 min.
        AMARELO   - Urgente. Situação que pode agravar. Atendimento em 60 min.
        VERDE     - Pouco urgente. Problema agudo mas não grave. Atendimento em 120 min.
        AZUL      - Não urgente. Situação crônica ou administrativa. Atendimento em 240 min.
        
        Regras críticas (aplicar obrigatoriamente):
        - Saturação O2 abaixo de 90% → VERMELHO automático.
        - Frequência cardíaca acima de 150 bpm ou abaixo de 40 bpm → VERMELHO.
        - Frequência respiratória acima de 30 irpm ou abaixo de 8 irpm → VERMELHO.
        - Temperatura acima de 41°C → VERMELHO.
        - Temperatura acima de 39.5°C em crianças menores de 2 anos → LARANJA.
        - Dor torácica com irradiação → LARANJA ou VERMELHO.
        - Glicemia abaixo de 60 ou acima de 400 mg/dL → LARANJA.
        - Gestante com dor abdominal intensa ou sangramento → LARANJA.
        - Sintomas piorando com esforço físico → aumentar um nível de urgência.
        - Se dados estiverem incompletos, classifique pelo cenário mais grave possível.
        - Sempre justifique com base nos sintomas e sinais vitais apresentados.
        - Responda APENAS em formato JSON válido, sem explicações fora do JSON.
        
        Formato obrigatório da resposta:
        {
          "cor": "VERMELHO|LARANJA|AMARELO|VERDE|AZUL",
          "prioridade": "texto da prioridade",
          "justificativa": "explicação clínica detalhada",
          "condutaRecomendada": "orientação para a equipe de enfermagem",
          "alertaUrgente": true|false,
          "tempoEspera": número_em_minutos
        }
    """)
    // o texto que será enviado para a IA analisar
    @UserMessage("""
        Realize a triagem do seguinte paciente conforme o Protocolo de Manchester:
        
        === IDENTIFICAÇÃO ===
        Nome: {{nome}}
        Idade: {{idade}} anos
        Sexo biológico: {{sexo}}
        Gestante: {{gestante}}
        Semanas de gestação: {{semanasGestacao}}
        
        === QUEIXA PRINCIPAL ===
        Queixa: {{queixaPrincipal}}
        Sintomas relatados: {{sintomas}}
        Local da dor: {{localDor}}
        Nível de dor (0-10): {{dor}}
        Início dos sintomas: {{inicioSintomas}}
        Evolução dos sintomas: {{evolucaoSintoma}}
        Piora com esforço: {{sintomaPioraEsforco}}
        
        === SINAIS VITAIS ===
        Temperatura: {{temperatura}}°C
        Pressão arterial: {{paSistolica}}/{{paDiastolica}} mmHg
        Saturação O2: {{saturacao}}%
        Frequência cardíaca: {{frequenciaCardiaca}} bpm
        Frequência respiratória: {{frequenciaRespiratoria}} irpm
        Peso: {{peso}} kg | Altura: {{altura}} cm | IMC: {{imc}}
        Glicemia: {{glicemia}} mg/dL
        
        === HISTÓRICO CLÍNICO ===
        Doenças preexistentes: {{doencas}}
        Medicamentos em uso: {{medicamentos}}
        Alergias: {{alergias}}
        Observações: {{observacoes}}
        
        Classifique conforme o Protocolo de Manchester.
    """)
    //O Mapeamento (@V): A anotação @V("nome") avisa ao LangChain4j que o valor
    // passado na variável Java nome deve ser injetado exatamente onde está escrito {{nome}} no seu @UserMessage.
    String classificarPaciente(
            @V("nome") String nome,
            @V("idade") int idade,
            @V("sexo") String sexo,
            @V("gestante") String gestante,
            @V("semanasGestacao") String semanasGestacao,
            @V("queixaPrincipal") String queixaPrincipal,
            @V("sintomas") String sintomas,
            @V("localDor") String localDor,
            @V("dor") String dor,
            @V("inicioSintomas") String inicioSintomas,
            @V("evolucaoSintoma") String evolucaoSintoma,
            @V("sintomaPioraEsforco") String sintomaPioraEsforco,
            @V("temperatura") String temperatura,
            @V("paSistolica") String paSistolica,
            @V("paDiastolica") String paDiastolica,
            @V("saturacao") String saturacao,
            @V("frequenciaCardiaca") String frequenciaCardiaca,
            @V("frequenciaRespiratoria") String frequenciaRespiratoria,
            @V("peso") String peso,
            @V("altura") String altura,
            @V("imc") String imc,
            @V("glicemia") String glicemia,
            @V("doencas") String doencas,
            @V("medicamentos") String medicamentos,
            @V("alergias") String alergias,
            @V("observacoes") String observacoes
    );
}