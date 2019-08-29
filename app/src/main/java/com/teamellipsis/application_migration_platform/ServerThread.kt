package com.teamellipsis.application_migration_platform

import com.teamellipsis.dynamic.DynamicApp

class ServerThred(myParam: String, private var dapp: DynamicApp?) : Thread() {
//    private val skserver: ? = null
    private val server: servr
    private var serverExist= false

    init {
        server = servr(dapp)
        serverExist=true
    }

    override fun run() {
        super.run()

        server.start()

    }

    fun setstate(state: String) {
//        this.server!!.setstate(state)
    }

    fun closeserver() {
        server.stop()
    }

    fun get_app(app: DynamicApp) {
        this.dapp = app
    }

}
