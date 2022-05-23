package com.example.mylibrary

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_edit_book.*
import kotlinx.android.synthetic.main.activity_edit_book.tv_rating
import kotlinx.android.synthetic.main.add_rate.*
import kotlinx.android.synthetic.main.add_rate.rating
import kotlinx.android.synthetic.main.item_book.*
import java.util.*
import kotlin.collections.ArrayList

class EditBookActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    var  isFavorite:Boolean = false
    var categoryName:String = ""
    var list_name_categories = ArrayList<String>()
    var adapter: ArrayAdapter<String>? = null
    var rate = 0.0
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_book)
        db = Firebase.firestore
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        var id = intent.getStringExtra("id")


        Thread{

            getInfoToBook(id.toString())


        }.start()
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.add_rate)

        add_rating_in_edit.setOnClickListener {
            //var rating2 =tv_rating.text as Float
//            try {
//                var rating2 =tv_rating.text as Float
//            }catch (e){
//
//            }
            bottomSheetDialog.rating.rating = rate.toFloat()

            bottomSheetDialog.show()

        }


        btn_back_in_edit.setOnClickListener {
            onBackPressed()
        }
        btn_cancle_in_edit.setOnClickListener {
//            val bottomSheetDialog = BottomSheetDialog(this)
//            bottomSheetDialog.setContentView(R.layout.cancle_and_back)
            onBackPressed()

        }


        bottomSheetDialog.rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rate = rating.toDouble()
            tv_rating.text = "$rating"

            // Toast.makeText(this, "rating is : $rating ", Toast.LENGTH_LONG).show()
        }


        btn_seve_and__edit.setOnClickListener {
            editBook(
                id.toString(), et_name_book_in_edit.text.toString(),
                etdescription_in_edit.text.toString(), et_name_author_in_edit.text.toString(),
                rate.toFloat(), sp_categories_in_edit.selectedItem.toString())
            //et_year_relese.text.toString(),
        }




    }
    fun getInfoToBook(id:String){
        db!!.collection("books").document("$id").get().addOnSuccessListener { snapShot ->
            var id = snapShot.id
            var name= snapShot["name"].toString()
            var desciption= snapShot["desciption"].toString()
            categoryName= snapShot["nameCategory"].toString()
            var rating= snapShot["rate"]
            var  authorName= snapShot["author"].toString()
            isFavorite= snapShot["isFavorite"] as Boolean
            var yearRelease= snapShot["yearRealese"]

                rate = rating as Double


                et_name_book_in_edit.setText(name)
                et_name_author_in_edit.setText(authorName)
                etdescription_in_edit.setText(desciption)
                tv_rating.setText(rating.toString())


            list_name_categories.add("$categoryName")
            getCategories()
//            Toast.makeText(this, "${id} \n $name \n $desciption \n $categoryName", Toast.LENGTH_LONG).show()
        }.addOnFailureListener { e ->

            }
    }

    fun getCategories() {
        db!!.collection("categories").get()
            .addOnSuccessListener { querySnapshot ->

               // list_name_categories[0] = categoryName
                for (i in 0 until querySnapshot.documents.size) {
                    if (querySnapshot.documents.get(i).get("name_category").toString() != categoryName) {
                        list_name_categories.add(
                            querySnapshot.documents.get(i).get("name_category").toString()
                        )

                    }
                }
                adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_name_categories)
                sp_categories_in_edit.adapter  = adapter
            }
    }

    private fun editBook(id:String, name:String, desciption:String, author:String, rate: Float, nameCategory:String ) { //yearRealese:String,
        if (id.isNotEmpty() && name.isNotEmpty()  && desciption.isNotEmpty()  && author.isNotEmpty() && rate != null
            && nameCategory.isNotEmpty()
        ){
            progressDialog.show()
//         var date= Date()
//         date.year = 2019
            val book = hashMapOf(
                "id" to id,
                "name" to name,
                "desciption" to desciption,
                "author" to author,
                "yearRealese" to Timestamp(Date()),
                "rate" to rate,
                "nameCategory" to nameCategory,
                "isFavorite" to isFavorite
            )

            db!!.collection("books").document(id)
                .set(book)
                .addOnSuccessListener { documentReference ->
                    // Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    progressDialog.dismiss()
                    onBackPressed()
                }
                .addOnFailureListener { e ->
                    // Log.w(TAG, "Error adding document", e)
                    progressDialog.dismiss()
                    Toast.makeText(this, "أعد المحاولة فشلت عملية حفظ البيانات", Toast.LENGTH_LONG).show()
                }
        }else{
            Toast.makeText(this, "يجب ملئ البيانات المطلوبة", Toast.LENGTH_LONG).show()
        }
    }

}