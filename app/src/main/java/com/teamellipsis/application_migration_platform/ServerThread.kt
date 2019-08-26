package com.teamellipsis.application_migration_platform

import com.teamellipsis.dynamic.DynamicApp

class ServerThred(myParam: String, private var dapp: DynamicApp?) : Thread() {
//    private val skserver: ? = null
    private val server: servr

    init {
        //        Todo_App t1 = new Todo_App();
        val arr = arrayOf("0", "test1", "test_task", "2001-2-21")
        val arr2 = arrayOf("0", "test1", "test_task", "2001-2-21")
        val arr3 = arrayOf("0", "test1", "test_task", "2001-2-21")
//        dapp!!.execute(arr)
//        dapp!!.execute(arr2)
//        dapp!!.
        //        String[] arr1 = {"1","2","2001-2-22"};
        //
        //        System.out.println(t1.execute(arr1));
        //        Gson gson=new Gson();
        //        String json=gson.toJson(t1.saveState());
        //
        //
        server = servr(dapp)


        //        skserver = new server(myParam);

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
