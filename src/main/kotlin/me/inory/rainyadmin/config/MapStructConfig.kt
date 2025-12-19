package me.inory.rainyadmin.config

import org.mapstruct.MapperConfig
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.NullValuePropertyMappingStrategy

@MapperConfig(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
class MapStructConfig