package com.example.parameter

import dagger.BindsInstance
import dagger.Component
import javax.inject.Named

@Component(modules = [MyModule::class])
interface CarComponent {
    fun getCar(): Car

    @Component.Builder
    interface Builder {
        fun setMyModule(myModule: MyModule): Builder

        @BindsInstance
        fun setMaterialOfProvides(@Named("material") country: String): Builder

        @BindsInstance
        fun setTypeOfProvides(@Named("type") type: String): Builder

        fun build(): CarComponent
    }
}