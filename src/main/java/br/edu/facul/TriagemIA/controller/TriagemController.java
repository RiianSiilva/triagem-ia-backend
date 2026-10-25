package br.edu.facul.TriagemIA.controller;

import br.edu.facul.TriagemIA.entity.Paciente;
import br.edu.facul.TriagemIA.entity.Triagem;
import br.edu.facul.TriagemIA.model.TriagemRequest;
import br.edu.facul.TriagemIA.model.TriagemResponse;
import br.edu.facul.TriagemIA.repository.PacienteRepository;
import br.edu.facul.TriagemIA.repository.TriagemRepository;
import br.edu.facul.TriagemIA.service.AlertaService;
import br.edu.facul.TriagemIA.service.TriagemAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
//Diz ao Spring que esta classe vai lidar com requisições HTTP (Web) e que todas as respostas devem ser enviadas diretamente no corpo da resposta
@RequestMapping("/api/triagem") //Define o endereço (URL) para chegar aqui
@CrossOrigin(origins = "*")
public class TriagemController {

    private final TriagemAiService triagemAiService;
    private final ObjectMapper objectMapper; // Uma ferramenta poderosa do Spring (da biblioteca Jackson). Ela serve para converter textos em formato JSON para objetos Java, e vice-versa.
    private final PacienteRepository pacienteRepository;
    private final TriagemRepository triagemRepository;
    private final AlertaService alertaService;

    public TriagemController(TriagemAiService triagemAiService, ObjectMapper objectMapper, PacienteRepository pacienteRepository, TriagemRepository triagemRepository, AlertaService alertaService) {
        this.triagemAiService = triagemAiService;
        this.objectMapper = objectMapper;
        this.pacienteRepository = pacienteRepository;
        this.triagemRepository = triagemRepository;
        this.alertaService = alertaService;
    }

    @PostMapping
    //Significa que este mtodo só aceita requisições do tipo POST (usado quando queremos enviar dados para o servidor processar).
    public ResponseEntity<TriagemResponse> realizarTriagem(@RequestBody TriagemRequest request) {
        try {
            // 1. Busca ou cria o paciente pelo CPF
            Paciente paciente = pacienteRepository
                    .findByCpf(request.cpf())
                    .orElseGet(() -> {
                        Paciente novo = new Paciente();
                        novo.setNome(request.nomePaciente());
                        novo.setCpf(request.cpf());
                        novo.setDataNascimento(request.dataNascimento());
                        novo.setSexoBiologico(request.sexoBiologico());
                        novo.setTelefoneContato(request.telefoneContato());
                        novo.setNomeResponsavel(request.nomeResponsavel());
                        return pacienteRepository.save(novo);
                    });

            // Calcula a idade a partir da data de nascimento
            int idade = Period.between(request.dataNascimento(), LocalDate.now()).getYears();

            // Tente processar os dados e chamar o serviço de IA
            // Calcula IMC para passar à IA
            String imcTexto = "não informado";
            if (request.peso() != null && request.altura() != null && request.altura() > 0) {
                double alturaEmMetros = request.altura() / 100.0;
                double imcCalculado = request.peso() / (alturaEmMetros * alturaEmMetros);
                imcTexto = String.format("%.1f", imcCalculado);
            }

            String jsonResposta = triagemAiService.classificarPaciente(
                    request.nomePaciente(),
                    idade,
                    request.sexoBiologico() != null ? request.sexoBiologico() : "não informado",
                    request.gestante() ? "Sim" : "Não",
                    request.semanasGestacao() != null ? request.semanasGestacao().toString() : "não se aplica",
                    request.queixaPrincipal() != null ? request.queixaPrincipal() : "não informada",
                    String.join(", ", request.sintomas()),
                    request.localDor() != null ? request.localDor() : "não informado",
                    request.nivelDor() != null ? request.nivelDor().toString() : "não informado",
                    request.inicioSintomas() != null ? request.inicioSintomas() : "não informado",
                    request.evolucaoSintomas() != null ? request.evolucaoSintomas() : "não informado",
                    request.sintomaPioraEsforco() ? "Sim" : "Não",
                    request.temperatura() != null ? request.temperatura().toString() : "não informada",
                    request.pressaoSistolica() != null ? request.pressaoSistolica().toString() : "não informada",
                    request.pressaoDiastolica() != null ? request.pressaoDiastolica().toString() : "não informada",
                    request.saturacaoO2() != null ? request.saturacaoO2().toString() : "não informada",
                    request.frequenciaCardiaca() != null ? request.frequenciaCardiaca().toString() : "não informada",
                    request.frequenciaRespiratoria() != null ? request.frequenciaRespiratoria().toString() : "não informada",
                    request.peso() != null ? request.peso().toString() : "não informado",
                    request.altura() != null ? request.altura().toString() : "não informado",
                    imcTexto,
                    request.glicemia() != null ? request.glicemia() : "não informada",
                    String.join(", ", request.doencasPreexistentes()),
                    String.join(", ", request.medicamentosEmUso()),
                    String.join(", ", request.alergias()),
                    request.observacoes() != null ? request.observacoes() : "nenhuma"
            );

            System.out.println("=== RESPOSTA BRUTA DO GROQ ===");
            System.out.println(jsonResposta);
            System.out.println("==============================");

            // Limpeza das tags Markdown da resposta
            jsonResposta = jsonResposta
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // Conversão do JSON para o objeto de resposta final
            TriagemResponse response = objectMapper.readValue(jsonResposta, TriagemResponse.class);

            // salva a triagem no banco
            Triagem triagem = new Triagem();

            // --- Dados da IA ---
            triagem.setPaciente(paciente);
            triagem.setCor(response.cor());
            triagem.setCorSugeridaIa(response.cor());
            triagem.setPrioridade(response.prioridade());
            triagem.setJustificativa(response.justificativa());
            triagem.setCondutaRecomendada(response.condutaRecomendada());
            triagem.setAlertaUrgente(response.alertaUrgente());
            triagem.setTempoEspera(response.tempoEspera());

            // --- ETAPA 2: Queixa principal ---
            triagem.setQueixaPrincipal(request.queixaPrincipal());
            triagem.setSintomas(String.join(", ", request.sintomas()));
            triagem.setLocalDor(request.localDor());
            triagem.setNivelDor(request.nivelDor() != null ? request.nivelDor().toString() : "não informado");
            triagem.setInicioSintomas(request.inicioSintomas());
            triagem.setEvolucaoSintoma(request.evolucaoSintomas());

            // --- ETAPA 3: Sinais vitais ---
            triagem.setTemperatura(request.temperatura());
            triagem.setPressaoSistolica(request.pressaoSistolica());
            triagem.setPressaoDiastolica(request.pressaoDiastolica());
            triagem.setSaturacaoO2(request.saturacaoO2());
            triagem.setFrequenciaCardiaca(request.frequenciaCardiaca());
            triagem.setFrequenciaRespiratoria(request.frequenciaRespiratoria());
            triagem.setPeso(request.peso());
            triagem.setAltura(request.altura());
            triagem.setGlicemia(request.glicemia());

            // Calcula IMC automaticamente se peso e altura foram informados
            if (request.peso() != null && request.altura() != null && request.altura() > 0) {
                double alturaEmMetros = request.altura() / 100.0;
                double imc = request.peso() / (alturaEmMetros * alturaEmMetros);
                triagem.setImc(Math.round(imc * 10.0) / 10.0);
            }

            // --- ETAPA 4: Histórico clínico ---
            triagem.setDoencasPreexistentes(String.join(", ", request.doencasPreexistentes()));
            triagem.setMedicamentosEmUso(String.join(", ", request.medicamentosEmUso()));
            triagem.setAlergias(String.join(", ", request.alergias()));
            triagem.setGestante(request.gestante());
            triagem.setSemanasGestacao(request.semanasGestacao());

            triagemRepository.save(triagem);

            // Envia alerta em tempo real se for VERMELHO ou LARANJA
            if (response.alertaUrgente()) {
                alertaService.enviarAlerta(
                        response.cor(),
                        paciente.getNome(),
                        String.join(", ", request.sintomas()),
                        triagem.getId()
                );
            }

            // Se tudo der certo, o Spring ignora o 'catch' e envia o resultado
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("=== ERRO ===");
            System.err.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }


        // O bloco try-catch é o tratamento de Exceções do java

        // REQUEST É O ENVELOPE QUE O FRONT-END PREENCHE E ENVIA PARA O BACKEND
        // RESPONSE É O ENVELOPE QUE O BACKEND PREENCHE E ENVIA PARA O FRONT-END

        // O Front-end envia um JSON combinando com o formato do TriagemRequest.
        // O Spring Boot lê esse JSON e cria o objeto Java request.
        // O seu Controller extrai os dados do request, limpa os valores e passa para a IA.
        // A IA gera um texto JSON brutos com o resultado.
        // O objectMapper pega esse texto da IA e o transforma em um objeto TriagemResponse.
        // O Controller devolve o TriagemResponse para o front-end, que chega na tela formatado

    }

    // Busca todas as triagens urgentes (vermelho e laranja) para o painel do médico
    @GetMapping("/urgentes")
    public ResponseEntity<List<Triagem>> getTriagensUrgentes() {
        List<Triagem> urgentes = triagemRepository
                .findByAlertaUrgenteOrderByDataTriagemDesc(true);
        return ResponseEntity.ok(urgentes);
    }

    // Busca triagens por cor
    @GetMapping("/cor/{cor}")
    public ResponseEntity<List<Triagem>> getTriagensPorCor(@PathVariable String cor) {
        List<Triagem> triagens = triagemRepository
                .findByCorOrderByDataTriagemDesc(cor.toUpperCase());
        return ResponseEntity.ok(triagens);
    }

    // Busca Historico de um pacciente
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<Triagem>> getTriagensPorPaciente(@PathVariable long id) {
        List<Triagem> triagens = triagemRepository
                .findByPacienteIdOrderByDataTriagemDesc(id);
        return ResponseEntity.ok(triagens);
    }

    // Lista todos os Pacientes
    @GetMapping("/pacientes")
    public ResponseEntity<List<Paciente>> getPacientes() {
        return ResponseEntity.ok(pacienteRepository.findAll());
    }

    // Busca triagens pelo CPF do paciente
    @GetMapping("/historico/{cpf}")
    public ResponseEntity<List<Triagem>> getHistoricoPorCpf(@PathVariable String cpf) {
        return pacienteRepository.findByCpf(cpf)
                .map(p -> ResponseEntity.ok(
                        triagemRepository.findByPacienteIdOrderByDataTriagemDesc(p.getId())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualiza status da triagem
    @PutMapping("/{id}/status")
    public ResponseEntity<Triagem> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return triagemRepository.findById(id)
                .map(t -> {
                    t.setStatus(status);
                    triagemRepository.save(t);
                    return ResponseEntity.ok(t);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // Endpoint para o profissional de saúde revisar/alterar a classificação da IA
    @PutMapping("/{id}/revisao")
    public ResponseEntity<Triagem> revisarTriagem(
            @PathVariable Long id,
            @RequestBody br.edu.facul.TriagemIA.dto.RevisaoRequest request) {

        return triagemRepository.findById(id)
                .map(triagem -> {
                    // Guarda a decisão e justificativa do profissional
                    triagem.setCorFinalProfissional(request.corFinalProfissional().toUpperCase());
                    triagem.setMotivoAlteracaoCor(request.motivoAlteracaoCor());
                    triagem.setNomeProfissional(request.nomeProfissional());
                    triagem.setDataRevisao(java.time.LocalDateTime.now());

                    // Atualiza a cor oficial de atendimento para a definida pelo profissional
                    triagem.setCor(request.corFinalProfissional().toUpperCase());
                    triagem.setStatus("REVISADO");

                    // Se o profissional elevou para VERMELHO ou LARANJA, marca alerta urgente
                    boolean urgente = "VERMELHO".equalsIgnoreCase(request.corFinalProfissional())
                            || "LARANJA".equalsIgnoreCase(request.corFinalProfissional());
                    triagem.setAlertaUrgente(urgente);

                    triagemRepository.save(triagem);
                    return ResponseEntity.ok(triagem);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para confirmar recebimento de alerta crítico (ACK)
    @PutMapping("/{id}/confirmar-alerta")
    public ResponseEntity<Triagem> confirmarAlerta(
            @PathVariable Long id,
            @RequestBody br.edu.facul.TriagemIA.dto.ConfirmarAlertaRequest request) {

        return triagemRepository.findById(id)
                .map(triagem -> {
                    triagem.setAlertaConfirmado(true);
                    triagem.setConfirmadoPor(request.nomeProfissional());
                    triagem.setDataConfirmacaoAlerta(java.time.LocalDateTime.now());
                    triagemRepository.save(triagem);
                    return ResponseEntity.ok(triagem);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    // Endpoint para Triagem em Modo de Contingência (quando a IA ou conexão estiver fora do ar)
    @PostMapping("/manual")
    public ResponseEntity<Triagem> realizarTriagemManual(@RequestBody br.edu.facul.TriagemIA.dto.TriagemManualRequest requestManual) {
        TriagemRequest request = requestManual.dadosTriagem();

        // 1. Busca ou cria o paciente
        Paciente paciente = pacienteRepository
                .findByCpf(request.cpf())
                .orElseGet(() -> {
                    Paciente novo = new Paciente();
                    novo.setNome(request.nomePaciente());
                    novo.setCpf(request.cpf());
                    novo.setDataNascimento(request.dataNascimento());
                    novo.setSexoBiologico(request.sexoBiologico());
                    novo.setTelefoneContato(request.telefoneContato());
                    novo.setNomeResponsavel(request.nomeResponsavel());
                    return pacienteRepository.save(novo);
                });

        // 2. Cria a triagem em contingência
        Triagem triagem = new Triagem();
        triagem.setPaciente(paciente);

        String corUpper = requestManual.corEscolhida().toUpperCase();
        triagem.setCor(corUpper);
        triagem.setCorFinalProfissional(corUpper);
        triagem.setCorSugeridaIa("MODO_CONTINGENCIA_MANUAL");
        triagem.setJustificativa(requestManual.justificativaClinica());
        triagem.setCondutaRecomendada(requestManual.condutaRecomendada());
        triagem.setNomeProfissional(requestManual.nomeProfissional());
        triagem.setDataRevisao(java.time.LocalDateTime.now());
        triagem.setModoContingencia(true);
        // Define prioridade e tempo pelo padrão Manchester
        boolean urgente = "VERMELHO".equals(corUpper) || "LARANJA".equals(corUpper);
        triagem.setAlertaUrgente(urgente);
        switch (corUpper) {
            case "VERMELHO" -> { triagem.setPrioridade("Emergência"); triagem.setTempoEspera(0); }
            case "LARANJA"  -> { triagem.setPrioridade("Muito Urgente"); triagem.setTempoEspera(10); }
            case "AMARELO"  -> { triagem.setPrioridade("Urgente"); triagem.setTempoEspera(60); }
            case "VERDE"    -> { triagem.setPrioridade("Pouco Urgente"); triagem.setTempoEspera(120); }
            default         -> { triagem.setPrioridade("Não Urgente"); triagem.setTempoEspera(240); }
        }

        // Salva dados clínicos
        triagem.setQueixaPrincipal(request.queixaPrincipal());
        triagem.setSintomas(String.join(", ", request.sintomas()));
        triagem.setLocalDor(request.localDor());
        triagem.setNivelDor(request.nivelDor() != null ? request.nivelDor().toString() : "não informado");
        triagem.setInicioSintomas(request.inicioSintomas());
        triagem.setEvolucaoSintoma(request.evolucaoSintomas());
        triagem.setTemperatura(request.temperatura());
        triagem.setPressaoSistolica(request.pressaoSistolica());
        triagem.setPressaoDiastolica(request.pressaoDiastolica());
        triagem.setSaturacaoO2(request.saturacaoO2());
        triagem.setFrequenciaCardiaca(request.frequenciaCardiaca());
        triagem.setFrequenciaRespiratoria(request.frequenciaRespiratoria());
        triagem.setPeso(request.peso());
        triagem.setAltura(request.altura());
        triagem.setGlicemia(request.glicemia());

        if (request.peso() != null && request.altura() != null && request.altura() > 0) {
            double alturaM = request.altura() / 100.0;
            triagem.setImc(Math.round((request.peso() / (alturaM * alturaM)) * 10.0) / 10.0);
        }

        triagem.setDoencasPreexistentes(String.join(", ", request.doencasPreexistentes()));
        triagem.setMedicamentosEmUso(String.join(", ", request.medicamentosEmUso()));
        triagem.setAlergias(String.join(", ", request.alergias()));
        triagem.setGestante(request.gestante());
        triagem.setSemanasGestacao(request.semanasGestacao());

        triagemRepository.save(triagem);

        // Se for Vermelho ou Laranja, dispara alerta WebSocket mesmo sendo manual!
        if (urgente) {
            alertaService.enviarAlerta(corUpper, paciente.getNome(), String.join(", ", request.sintomas()), triagem.getId());
        }

        return ResponseEntity.ok(triagem);
    }

    // Busca todas as triagens ordenadas por data
    @GetMapping("/todas")
    public ResponseEntity<List<Triagem>> getTodasTriagens() {
        return ResponseEntity.ok(
                triagemRepository.findAll()
                        .stream()
                        .sorted((a, b) -> b.getDataTriagem().compareTo(a.getDataTriagem()))
                        .toList()
        );
    }


}