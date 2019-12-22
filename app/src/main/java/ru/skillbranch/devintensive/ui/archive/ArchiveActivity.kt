package ru.skillbranch.devintensive.ui.archive

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.custom.ChatItemDecoration
import ru.skillbranch.devintensive.viewmodels.ArchiveViewModel


class ArchiveActivity : AppCompatActivity() {
    private lateinit var viewModel: ArchiveViewModel
    private lateinit var archiveAdapter: ChatAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        initToolBar()
        initViews()
        initViewModel()


    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArchiveViewModel::class.java)
        viewModel.getArchiveItems().observe(this, Observer {
            archiveAdapter.updateData(it)
        })
    }

    private fun initToolBar() {
        setSupportActionBar(toolbar_archive)
        supportActionBar?.title = "Архив чатов"
    }

    private fun initViews() {
        archiveAdapter = ChatAdapter{
            Toast.makeText(this,"Click on ${it.title}",Toast.LENGTH_SHORT).show()
        }
        val divider = ChatItemDecoration(this)

        val touchCallback = ChatItemTouchHelperCallback(archiveAdapter,true){showSnackBarAction(it)}

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list){
            adapter = archiveAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }


    }

    private fun showSnackBarAction(item: ChatItem) {
        val itemId = item.id
        viewModel.restoreFromArchive(itemId)
        val snackBar = Snackbar.make(
            rv_archive_list,
            "Вы точно хотите восстановить ${item.title} из архива?",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("отмена") {
            viewModel.addToArchive(itemId)
            Snackbar.make(
                rv_archive_list,
                "Данные не восстановлены из архива",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        snackBar.show()
    }
}
