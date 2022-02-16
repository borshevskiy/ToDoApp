package com.borshevskiy.todoapp.fragments.update

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.borshevskiy.todoapp.R
import com.borshevskiy.todoapp.data.models.Priority
import com.borshevskiy.todoapp.data.models.ToDoData
import com.borshevskiy.todoapp.data.viewmodel.SharedViewModel
import com.borshevskiy.todoapp.data.viewmodel.ToDoViewModel
import com.borshevskiy.todoapp.databinding.FragmentUpdateBinding

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var toDoViewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        toDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)
        with(binding) {
            currentTitleEt.setText(args.currentItem.title)
            currentDescriptionEt.setText(args.currentItem.description)
            currentPrioritiesSpinner.setSelection(sharedViewModel.parsePriorityToInt(args.currentItem.priority))
            currentPrioritiesSpinner.onItemSelectedListener = sharedViewModel.listener
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_save) {
            updateItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        with(binding) {
            val title = currentTitleEt.text.toString()
            val priority = currentPrioritiesSpinner.selectedItem.toString()
            val description = currentDescriptionEt.text.toString()
            val validation = sharedViewModel.verifyDataFromUser(title, description)
            if (validation) {
                toDoViewModel.updateData(ToDoData(args.currentItem.id,title, sharedViewModel.parsePriority(priority),description))
                Toast.makeText(requireContext(),"Successfully updated!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            } else {
                Toast.makeText(requireContext(),"Please fill out all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}