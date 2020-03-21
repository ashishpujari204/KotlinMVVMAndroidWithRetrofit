package customview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatButton
import com.app.smartprocessors.util.Constants


class ButtonRegular : AppCompatButton {
    /**
     * custom views
     * @param context
     */

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        this.isInEditMode
        val tf = Typeface.createFromAsset(context.assets, Constants.font_name)
        typeface = tf
    }

}
