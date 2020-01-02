package ru.skillbranch.devintensive.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.getColorByThemeAttr
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.archive.ArchiveActivity
import ru.skillbranch.devintensive.ui.custom.ChatItemDecoration
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter : ChatAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolBar()
        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val menuItem = menu?.findItem(R.id.action_search)
        val actionView = menuItem?.actionView as SearchView

        actionView.queryHint = "Введите имя пользователя"

        actionView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }
        })


        return super.onCreateOptionsMenu(menu)
    }

    private fun initViews() {
        //delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES

        chatAdapter = ChatAdapter{

            if (it.chatType == ChatType.ARCHIVE){
                val intent = Intent(this, ArchiveActivity::class.java)
                startActivity(intent)
            } else
            showSnackBarMessage("Click on ${it.title}",Snackbar.LENGTH_SHORT)
        }
//        val divider = ChatItemDecoration(this)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.divider,theme))

        val touchCallback = ChatItemTouchHelperCallback(chatAdapter){
            val itemId = it.id
            viewModel.addToArchive(itemId)
            showSnackBarMessage("Вы точно хотите добавить ${it.title} в архив?"){
                viewModel.restoreFromArchive(itemId)
                showSnackBarMessage("Данные не добавлены в архив")
            }
        }

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_chat_list)

        with(rv_chat_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }



    }


    private fun initToolBar() {
        setSupportActionBar(toolbar)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(this, Observer {
            chatAdapter.updateData(it) })
    }

    private fun showSnackBarMessage(message:String,duration: Int=Snackbar.LENGTH_SHORT): Snackbar{
        val snackBar = Snackbar.make(rv_chat_list, message, duration)
        val view = snackBar.view
        val backgroundColor = view.getColorByThemeAttr(R.attr.colorSnackbarBackground)
        val textColor = view.getColorByThemeAttr(R.attr.colorSnackbarText)
        val textView =view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)
        view.setBackgroundColor(backgroundColor)

        snackBar.show()
        return snackBar
    }
    private fun showSnackBarMessage(message:String,action: (MainViewModel)-> Unit){
        showSnackBarMessage(message, Snackbar.LENGTH_LONG).apply {
            setAction("Отмена"){
                action.invoke(viewModel)
            }
        }
    }
}
