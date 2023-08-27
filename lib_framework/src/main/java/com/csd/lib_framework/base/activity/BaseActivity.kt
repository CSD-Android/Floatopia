package com.csd.lib_framework.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.therouter.TheRouter

/**
 *
 *
 *
 * @author Gleamrise
 * <br/>Created: 2023/08/03
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
    }

}