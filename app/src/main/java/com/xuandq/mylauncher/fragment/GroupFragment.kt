package com.xuandq.mylauncher.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuandq.mylauncher.R
import com.xuandq.mylauncher.activity.MainActivity
import com.xuandq.mylauncher.adapter.AppAdapter
import com.xuandq.mylauncher.model.Item
import com.xuandq.mylauncher.utils.DragListener
import com.xuandq.mylauncher.utils.Tool
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_group.*

class GroupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var itemGroup : Item? = null
    private var page : Int? = null
    private var groupPos : Int? = null
    private lateinit var appAdapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemGroup = it.getParcelable(ARG_PARAM1)
            page = it.getInt(ARG_PARAM2)
            groupPos = it.getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back_ground = view.findViewById<ConstraintLayout>(R.id.dialog_background)
        val recyc_dialog = view.findViewById<RecyclerView>(R.id.recyc_dialog)

        val gridManager = GridLayoutManager(requireContext(),3)
        recyc_dialog.layoutManager = gridManager
        appAdapter = AppAdapter(requireContext(), itemGroup?.items!!, false, 3)
        recyc_dialog.adapter = appAdapter

        appAdapter.setItemClickListenner {
            val intent = itemGroup?.items!!.get(it).intent
            startActivity(intent)
        }

        bound_dialog.setTag(R.id.page, page)
        bound_dialog.setTag(R.id.group_position, groupPos)
        bound_dialog.setOnDragListener(DragListener(requireActivity() as MainActivity))
        back_ground.setOnClickListener {
            (requireActivity() as MainActivity).hideDialogGroup()
        }



//        back_ground.viewTreeObserver.addOnGlobalLayoutListener {
//            val bg = RoundedBitmapDrawableFactory.create(
//                resources,
//                Tool.createBackGroundView(requireContext(),requireActivity().window.decorView,back_ground)
//            )
//
//            back_ground.background = bg
//        }
    }

    companion object {

        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"
        const val ARG_PARAM3 = "param3"

        @JvmStatic
        fun newInstance(itemGroup : Item, page : Int, groupPos : Int) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, itemGroup)
                    putInt(ARG_PARAM2, page)
                    putInt(ARG_PARAM3, groupPos)
                }
            }
    }
}