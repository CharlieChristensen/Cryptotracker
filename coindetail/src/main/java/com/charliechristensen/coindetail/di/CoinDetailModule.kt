package com.charliechristensen.coindetail.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_CoinDetailModule::class])
object CoinDetailModule