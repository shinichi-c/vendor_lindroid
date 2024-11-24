package org.lindroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.lindroid.ui.ContainerAdapter.ViewHolder

class ContainerAdapter(
	val context: Context,
	firstData: Pair<List<String>, List<Boolean>>,
	val startContainer: (String) -> Unit,
	val stopContainer: (String) -> Unit,
	val createContainer: (String) -> Unit,
	val deleteContainer: (String) -> Unit,
) : RecyclerView.Adapter<ViewHolder>() {
	var data = firstData
		@SuppressLint("NotifyDataSetChanged")
		set(value) {
			if (field != value) {
				field = value
				notifyDataSetChanged()
			}
		}
	private val containers
		get() = data.first
	private val running
		get() = data.second
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		if (viewType == 1) {
			return AddViewHolder(
				LayoutInflater.from(context)
					.inflate(android.R.layout.simple_list_item_1, parent, false)
			)
		}
		return ContainerViewHolder(
			LayoutInflater.from(context)
				.inflate(android.R.layout.simple_list_item_2, parent, false)
		)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		if (holder is ContainerViewHolder) {
			holder.name = containers[position]
			holder.running = running[position]
		}
	}

	override fun getItemCount(): Int {
		return containers.size + 1
	}

	override fun getItemViewType(position: Int): Int {
		return if (position == itemCount - 1) 1 else 0
	}

	abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
		View.OnClickListener {
		init {
			itemView.setOnClickListener(this)
		}

		abstract override fun onClick(v: View)
	}

	private inner class ContainerViewHolder(root: View) : ViewHolder(root),
		View.OnClickListener, OnLongClickListener {
		val mContainerName: TextView
		val mRunningIndicator: TextView
		var name: String? = null
			set(value) {
				if (field != value) {
					mContainerName.text = value
					field = value
				}
			}
		var running: Boolean = false
			set(value) {
				if (field != value) {
					mRunningIndicator.text =
						itemView.context.getString(if (value) R.string.running else R.string.not_running)
					field = value
				}
			}

		init {
			root.setOnLongClickListener(this)
			mContainerName = root.requireViewById(android.R.id.text1)
			mRunningIndicator = root.requireViewById<TextView>(android.R.id.text2).apply {
				text = itemView.context.getString(R.string.not_running)
			}
		}

		override fun onClick(v: View) {
			if (running) {
				MaterialAlertDialogBuilder(context)
					.setTitle(R.string.stop_container)
					.setMessage(context.getString(R.string.stop_container_msg, name))
					.setPositiveButton(android.R.string.ok) { _, _ ->
						stopContainer(name!!)
					}
					.setNegativeButton(R.string.no) { _, _ -> }
					.setCancelable(false)
					.setIcon(R.drawable.ic_warning)
					.show()
			} else {
				startContainer(name!!)
			}
		}

		override fun onLongClick(v: View): Boolean {
			if (!running) {
				MaterialAlertDialogBuilder(context)
					.setTitle(R.string.delete_container)
					.setMessage(context.getString(R.string.delete_container_msg, name))
					.setPositiveButton(R.string.yes) { _, _ ->
						deleteContainer(name!!)
					}
					.setNegativeButton(R.string.no) { _, _ -> }
					.setCancelable(false)
					.setIcon(R.drawable.ic_warning)
					.show()
			}
			return true
		}
	}

	private inner class AddViewHolder(root: View) : ViewHolder(root), View.OnClickListener {
		init {
			(root as TextView).setText(R.string.add_container)
		}

		override fun onClick(v: View) {
			// TODO make edittext red when user enters junk that perspectived doesn't like
			// (it checks if name is [0-9a-zA-Z])
			val editText = AppCompatEditText(context)
			MaterialAlertDialogBuilder(context)
				.setTitle(R.string.set_name)
				.setView(editText)
				.setPositiveButton(android.R.string.ok) { _, _ ->
					createContainer(editText.text!!.toString())
				}
				.setNegativeButton(android.R.string.cancel) { _, _ -> }
				.setIcon(R.drawable.ic_help)
				.show()
		}
	}
}