package STCM;

public interface OnRead
{
    void Completo(String RispostaCompleta, String id, int TipoComando, boolean nessunComandoInCoda);

    void Temporaneo(String RispostaTemporanea, String id);
    
    void ErroreWriteComandoInCoda(int ritorno);
}
