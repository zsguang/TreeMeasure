package com.example.treemeasure.treeHeight

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.example.treemeasure.Dao.AppDatabase
import com.example.treemeasure.Dao.TreeHeight
import com.example.treemeasure.Dao.TreeHeightDao
import com.example.treemeasure.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TreeHeightViewModel : ViewModel() {

    /** ORM对象关系映像 数据访问对象 */
    private val treeHeightDao: TreeHeightDao by lazy {
        AppDatabase.getDatabase(MyApplication.context).treeHeightDao()
    }

    private var _treeHeightList = MutableLiveData<List<TreeHeight>>()
    val treeHeightList: LiveData<List<TreeHeight>> get() = _treeHeightList

    init {
        getTreeHeightList()
        Log.i("TreeHeightViewModel", "ClothesReservation Model start")
    }

    private fun getTreeHeightList() {
        viewModelScope.launch(Dispatchers.IO) {
            _treeHeightList.postValue(treeHeightDao.loadAllTreeHeights())
        }
    }

}