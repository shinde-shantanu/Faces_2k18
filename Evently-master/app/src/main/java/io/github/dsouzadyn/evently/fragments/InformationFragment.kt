package io.github.dsouzadyn.evently.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.github.dsouzadyn.evently.R
import us.feras.mdv.MarkdownView


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [InformationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [InformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InformationFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mInfo: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mInfo = arguments.getString(ARG_INFO)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_information, container, false)
        val markdownView = view.findViewById<MarkdownView>(R.id.markdownText)
        val timandpum = view.findViewById<ImageView>(R.id.timanpum)
        val backg = view.findViewById<ImageView>(R.id.back)
        if (mInfo == "college")
            markdownView.loadMarkdownFile(getString(R.string.info_college))
        else if (mInfo == "etamax") {
            timandpum.visibility = View.VISIBLE
            backg.visibility = View.VISIBLE
            markdownView.loadMarkdownFile(getString(R.string.info_etamax))
        }
        else if (mInfo == "theme")
            markdownView.loadMarkdownFile(getString(R.string.info_theme))
        else {}
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }




    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_INFO = "INFO_PARAM"



        // TODO: Rename and change types and number of parameters
        fun newInstance(info: String): InformationFragment {
            val fragment = InformationFragment()
            val args = Bundle()
            args.putString(ARG_INFO, info)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
