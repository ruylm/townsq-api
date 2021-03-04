package com.townsq.test.enums;

public enum PermissaoEnum {
	
    NENHUMA(0),
    LEITURA(1),
    ESCRITA(2);

    private Integer codigo;

    PermissaoEnum(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

}
