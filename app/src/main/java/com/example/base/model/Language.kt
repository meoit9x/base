package com.example.base.model

import com.example.base.base.BaseModel

data class Language(
    var language: String? = null,
    var isSelected: Boolean = false,
    var value: String? = null,
    var flags: Int? = null
) : BaseModel()