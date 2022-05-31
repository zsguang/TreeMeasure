package com.example.treemeasure.treeCrown

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treemeasure.Dao.AppDatabase
import com.example.treemeasure.Dao.TreeCrown
import com.example.treemeasure.Dao.TreeCrownDao
import com.example.treemeasure.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TreeCrownViewModel : ViewModel() {

    /** ORM对象关系映像 数据访问对象 */
    private val treeCrownDao: TreeCrownDao by lazy {
        AppDatabase.getDatabase(MyApplication.context).treeCrownDao()
    }

    private var _treeCrownList = MutableLiveData<List<TreeCrown>>()
    val treeCrownList: LiveData<List<TreeCrown>> get() = _treeCrownList

    init {
        getTreeCrownList()
        Log.i("TreeCrownViewModel", "TreeCrownViewModel start")
    }

    private fun getTreeCrownList() {
        viewModelScope.launch(Dispatchers.IO) {
            _treeCrownList.postValue(treeCrownDao.loadAllTreeCrowns())
        }
    }
}