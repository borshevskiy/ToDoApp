package com.borshevskiy.todoapp.fragments.list

import android.app.AlertDialog
import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.borshevskiy.todoapp.R
import com.borshevskiy.todoapp.data.viewmodel.SharedViewModel
import com.borshevskiy.todoapp.data.viewmodel.ToDoViewModel
import com.borshevskiy.todoapp.databinding.FragmentListBinding

class ListFragment : Fragment() {

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
            R.id.menu_sortBy -> TODO()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
}