package com.borshevskiy.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.borshevskiy.todoapp.R
import com.borshevskiy.todoapp.data.models.ToDoData
import com.borshevskiy.todoapp.data.viewmodel.SharedViewModel
import com.borshevskiy.todoapp.data.viewmodel.ToDoViewModel
import com.borshevskiy.todoapp.databinding.FragmentListBinding
import com.borshevskiy.todoapp.fragments.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val mAdapter by lazy { ListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        toDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater,container,false)
        setupRecyclerView()
        toDoViewModel.getAllData.observe(viewLifecycleOwner, {
            sharedViewModel.checkIfDatabaseEmpty(it)
            mAdapter.setData(it)
        })
        sharedViewModel.emptyDatabase.observe(viewLifecycleOwner, {
            showEmptyDatabaseViews(it)
        })
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val searchView = menu.findItem(R.id.menu_search).actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
            R.id.menu_priority_high -> toDoViewModel.sortByHighPriority.observe(this, {
                mAdapter.setData(it)
            })
            R.id.menu_priority_low -> toDoViewModel.sortByLowPriority.observe(this, {
                mAdapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            itemAnimator = LandingAnimator().apply { addDuration = 300 }
        }
        swipeToDelete(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = mAdapter.dataList[viewHolder.adapterPosition]
                toDoViewModel.deleteData(deletedItem)
                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeletedData(binding.recyclerView,deletedItem)
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(view,"Deleted '${deletedItem.title}'",Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            toDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    private fun showEmptyDatabaseViews(emptyDatabase: Boolean) {
        with(binding) {
            if (emptyDatabase) {
                noDataImageView.visibility = View.VISIBLE
                noDataTextView.visibility = View.VISIBLE
            } else {
                noDataImageView.visibility = View.INVISIBLE
                noDataTextView.visibility = View.INVISIBLE
            }
        }
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_,_ ->
            toDoViewModel.deleteAll()
            Toast.makeText(requireContext(),"Successfully removed everything!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") {_,_ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure want to remove everything?")
        builder.create().show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) searchThroughDatabase(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) searchThroughDatabase(query)
        return true
    }

    private fun searchThroughDatabase(query: String) {
        toDoViewModel.searchDatabase("%$query%").observe(this, {
            it?.let { mAdapter.setData(it) }
        })
    }
}