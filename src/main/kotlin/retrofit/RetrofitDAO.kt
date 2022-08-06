package com.astrainteractive.astratemplate.auto_module.retrofit

import com.astrainteractive.astratemplate.auto_module.api.ProxyTask
import com.astrainteractive.astratemplate.auto_module.api.Response
import retrofit.models.CharacterRequest

data class Api(
    val characters: String = "",
    val locations: String = "",
    val episodes: String = "",
)

interface RetrofitDAO {
    @Get("api")
    fun getApi(): ProxyTask<Response<Api>>


    @Get("api/character")
    fun getCharacters(
        nonAnnotated: Any = "",
        @Query("count") count: Int = 1,
        @Query("page") page: Int = 2,
    ): ProxyTask<Response<CharacterRequest>>
}