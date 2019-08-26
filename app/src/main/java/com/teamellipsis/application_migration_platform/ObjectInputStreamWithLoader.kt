package com.teamellipsis.application_migration_platform

import java.io.*

internal class ObjectInputStreamWithLoader
/**
 * Loader must be non-null;
 */
@Throws(IOException::class, StreamCorruptedException::class)
constructor(`in`: InputStream, private val loader: java.lang.ClassLoader?) : ObjectInputStream(`in`) {

    init {
        if (loader == null) {
            throw IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader")
        }
    }

    /**
     * Use the given ClassLoader rather than using the system class
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    override fun resolveClass(classDesc: ObjectStreamClass): Class<*> {

        val cname = classDesc.name
        return Class.forName(classDesc.name, false, this.loader)
        //        return this.loader.loadClass(DynamicApp.class.getName());
    }
}

//            com.example.dynamicclassloader.Todo_App
////            com.example.dynamicclassloader.DynamicApp