package br.dev.brunoxkk0.airborder.api;

public class AirBorderAPI {

    private static BorderProvider BORDER_PROVIDER = null;

    public static BorderProvider getBorderProvider(){
        return BORDER_PROVIDER;
    }

    public static BorderProvider setBorderProvider(BorderProvider borderProvider){
        return BORDER_PROVIDER = borderProvider;
    }

}
