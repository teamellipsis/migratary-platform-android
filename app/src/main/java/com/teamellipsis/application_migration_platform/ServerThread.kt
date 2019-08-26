package com.teamellipsis.application_migration_platform

import com.teamellipsis.dynamic.DynamicApp

class ServerThred(myParam: String, private var dapp: DynamicApp?) : Thread() {
//    private val skserver: ? = null
    private val server: servr

    init {
        server = servr(dapp)
    }

    override fun run() {
        super.run()

        server.start()

    }

    fun setstate(state: String) {
//        this.server!!.setstate(state)
    }

    fun getstate() {
//        return this.server!!.getstate()
    }

    fun get_app(app: DynamicApp) {
        this.dapp = app
    }

}
