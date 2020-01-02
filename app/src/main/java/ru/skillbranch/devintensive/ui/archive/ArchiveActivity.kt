package ru.skillbranch.devintensive.ui.archive

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.getColorByThemeAttr
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
        //delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        archiveAdapter = ChatAdapter{
            Toast.makeText(this,"Click on ${it.title}",Toast.LENGTH_SHORT).show()
        }
        //val divider = ChatItemDecoration(this)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider,theme))
        val touchCallback = ChatItemTouchHelperCallback(archiveAdapter,true){
            val itemId = it.id
            viewModel.restoreFromArchive(itemId)
            showSnackBarMessage("Вы точно хотите восстановить ${it.title} из архива?"){
                viewModel.addToArchive(itemId)
                showSnackBarMessage("Данные не восстановлены из архива")
            }
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list){
            adapter = archiveAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }


    }

    private fun showSnackBarMessage(message:String,duration: Int = Snackbar.LENGTH_SHORT): Snackbar{
        val snackBar = Snackbar.make(rv_archive_list, message, duration)
        val view = snackBar.view
        val backgroundColor = view.getColorByThemeAttr(R.attr.colorSnackbarBackground)
        val textColor = view.getColorByThemeAttr(R.attr.colorSnackbarText)
        val textView =view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)
        view.setBackgroundColor(backgroundColor)
        snackBar.show()
        return snackBar
    }
    private fun showSnackBarMessage(message:String,action: (ArchiveViewModel)-> Unit){
        showSnackBarMessage(message, Snackbar.LENGTH_LONG).apply {
            setAction("Отмена"){
                action.invoke(viewModel)
            }
        }
    }
}
