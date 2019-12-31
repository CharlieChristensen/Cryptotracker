package com.charliechristensen.coinlist.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_CoinListModule::class])
object CoinListModule
