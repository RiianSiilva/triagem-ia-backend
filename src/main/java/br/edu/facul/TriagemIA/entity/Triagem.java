package br.edu.facul.TriagemIA.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "triagens")
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private String cor;
    private String prioridade;

    @Column(length = 1000)
    private String justificativa;

    @Column(name = "conduta_recomendada", length = 1000)
    private String condutaRecomendada;

    @Column(name = "alerta_urgente")
    private boolean alertaUrgente;

    @Column(name = "tempo_espera")
    private int tempoEspera;

    @Column(name = "sintomas", length = 500)
    private String sintomas;

    @Column(name = "nivel_dor")
    private String nivelDor;

    private Double temperatura;

    @Column(name = "pressao_sistolica")
    private Integer pressaoSistolica;

    @Column(name = "pressao_diastolica")
    private Integer pressaoDiastolica;

    @Column(name = "saturacao_o2")
    private Integer saturacaoO2;

    @Column(name = "data_triagem")
    private LocalDateTime dataTriagem;

    @Column
    private String status;

    @Column(name = "frequencia_cardiaca")
    private Integer frequenciaCardiaca;

    @Column(name = "frequencia_respiratoria")
    private Integer frequenciaRespiratoria;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "altura")
    private Double altura;

    @Column(name = "imc")
    private Double imc;

    @Column(name = "glicemia")
    private String glicemia;

    @Column(name = "queixa_principal", length = 1000)
    private String queixaPrincipal;

    @Column(name = "local_dor")
    private String localDor;

    @Column(name = "inicio_sintomas")
    private String inicioSintomas;

    @Column(name = "evolucao_sintoma")
    private String evolucaoSintoma;

    @Column(name = "doencas_preexistentes", length = 500)
    private String doencasPreexistentes;

    @Column(name = "medicamentos_em_uso", length = 500)
    private String medicamentosEmUso;

    @Column(name = "alergias", length = 300)
    private String alergias;

    @Column(name = "gestante")
    private boolean gestante;

    @Column(name = "semanas_gestacao")
    private Integer semanasGestacao;

    @Column(name = "cor_sugerida_ia")
    private String corSugeridaIa;

    @Column(name = "cor_final_profissional")
    private String corFinalProfissional;

    @Column(name = "motivo_alteracao_cor", length = 500)
    private String motivoAlteracaoCor;

    @Column(name = "nome_profissional")
    private String nomeProfissional;

    @Column(name = "data_revisao")
    private LocalDateTime dataRevisao;

    @Column(name = "alerta_confirmado")
    private boolean alertaConfirmado = false;

    @Column(name = "confirmado_por")
    private String confirmadoPor;

    @Column(name = "data_confirmacao_alerta")
    private LocalDateTime dataConfirmacaoAlerta;

    @Column(name = "modo_contingencia")
    private boolean modoContingencia = false;

    @PrePersist
    public void prePersist() {
        this.dataTriagem = LocalDateTime.now();
        this.status = "AGUARDANDO";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getCondutaRecomendada() {
        return condutaRecomendada;
    }

    public void setCondutaRecomendada(String condutaRecomendada) {
        this.condutaRecomendada = condutaRecomendada;
    }

    public boolean isAlertaUrgente() {
        return alertaUrgente;
    }

    public void setAlertaUrgente(boolean alertaUrgente) {
        this.alertaUrgente = alertaUrgente;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(int tempoEspera) {
        this.tempoEspera = tempoEspera;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getNivelDor() {
        return nivelDor;
    }

    public void setNivelDor(String nivelDor) {
        this.nivelDor = nivelDor;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getPressaoSistolica() {
        return pressaoSistolica;
    }

    public void setPressaoSistolica(Integer pressaoSistolica) {
        this.pressaoSistolica = pressaoSistolica;
    }

    public Integer getPressaoDiastolica() {
        return pressaoDiastolica;
    }

    public void setPressaoDiastolica(Integer pressaoDiastolica) {
        this.pressaoDiastolica = pressaoDiastolica;
    }

    public Integer getSaturacaoO2() {
        return saturacaoO2;
    }

    public void setSaturacaoO2(Integer saturacaoO2) {
        this.saturacaoO2 = saturacaoO2;
    }

    public LocalDateTime getDataTriagem() {
        return dataTriagem;
    }

    public void setDataTriagem(LocalDateTime dataTriagem) {
        this.dataTriagem = dataTriagem;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getFrequenciaCardiaca() {
        return frequenciaCardiaca;
    }

    public void setFrequenciaCardiaca(Integer frequenciaCardiaca) {
        this.frequenciaCardiaca = frequenciaCardiaca;
    }

    public Integer getFrequenciaRespiratoria() {
        return frequenciaRespiratoria;
    }

    public void setFrequenciaRespiratoria(Integer frequenciaRespiratoria) {
        this.frequenciaRespiratoria = frequenciaRespiratoria;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Double getImc() {
        return imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public String getGlicemia() {
        return glicemia;
    }

    public void setGlicemia(String glicemia) {
        this.glicemia = glicemia;
    }

    public String getQueixaPrincipal() {
        return queixaPrincipal;
    }

    public void setQueixaPrincipal(String queixaPrincipal) {
        this.queixaPrincipal = queixaPrincipal;
    }

    public String getLocalDor() {
        return localDor;
    }

    public void setLocalDor(String localDor) {
        this.localDor = localDor;
    }

    public String getInicioSintomas() {
        return inicioSintomas;
    }

    public void setInicioSintomas(String inicioSintomas) {
        this.inicioSintomas = inicioSintomas;
    }

    public String getEvolucaoSintoma() {
        return evolucaoSintoma;
    }

    public void setEvolucaoSintoma(String evolucaoSintoma) {
        this.evolucaoSintoma = evolucaoSintoma;
    }

    public String getDoencasPreexistentes() {
        return doencasPreexistentes;
    }

    public void setDoencasPreexistentes(String doencasPreexistentes) {
        this.doencasPreexistentes = doencasPreexistentes;
    }

    public String getMedicamentosEmUso() {
        return medicamentosEmUso;
    }

    public void setMedicamentosEmUso(String medicamentosEmUso) {
        this.medicamentosEmUso = medicamentosEmUso;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public boolean isGestante() {
        return gestante;
    }

    public void setGestante(boolean gestante) {
        this.gestante = gestante;
    }

    public Integer getSemanasGestacao() {
        return semanasGestacao;
    }

    public void setSemanasGestacao(Integer semanasGestacao) {
        this.semanasGestacao = semanasGestacao;
    }

    public String getCorSugeridaIa() {
        return corSugeridaIa;
    }

    public void setCorSugeridaIa(String corSugeridaIa) {
        this.corSugeridaIa = corSugeridaIa;
    }

    public String getCorFinalProfissional() {
        return corFinalProfissional;
    }

    public void setCorFinalProfissional(String corFinalProfissional) {
        this.corFinalProfissional = corFinalProfissional;
    }

    public String getMotivoAlteracaoCor() {
        return motivoAlteracaoCor;
    }

    public void setMotivoAlteracaoCor(String motivoAlteracaoCor) {
        this.motivoAlteracaoCor = motivoAlteracaoCor;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public LocalDateTime getDataRevisao() {
        return dataRevisao;
    }

    public void setDataRevisao(LocalDateTime dataRevisao) {
        this.dataRevisao = dataRevisao;
    }

    public boolean isAlertaConfirmado() {
        return alertaConfirmado;
    }

    public void setAlertaConfirmado(boolean alertaConfirmado) {
        this.alertaConfirmado = alertaConfirmado;
    }

    public String getConfirmadoPor() {
        return confirmadoPor;
    }

    public void setConfirmadoPor(String confirmadoPor) {
        this.confirmadoPor = confirmadoPor;
    }

    public LocalDateTime getDataConfirmacaoAlerta() {
        return dataConfirmacaoAlerta;
    }

    public void setDataConfirmacaoAlerta(LocalDateTime dataConfirmacaoAlerta) {
        this.dataConfirmacaoAlerta = dataConfirmacaoAlerta;
    }

    public boolean isModoContingencia() {
        return modoContingencia;
    }

    public void setModoContingencia(boolean modoContingencia) {
        this.modoContingencia = modoContingencia;
    }
}
