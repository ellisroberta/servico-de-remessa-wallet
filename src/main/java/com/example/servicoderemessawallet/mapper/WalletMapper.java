package com.example.servicoderemessawallet.mapper;

import com.example.servicoderemessawallet.dto.WalletDTO;
import com.example.servicoderemessawallet.model.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    Wallet toEntity(WalletDTO dto);
    WalletDTO toDto(Wallet entity);
}
