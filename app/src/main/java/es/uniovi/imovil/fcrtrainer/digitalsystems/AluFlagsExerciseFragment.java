package es.uniovi.imovil.fcrtrainer.digitalsystems;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;

/**
 * Created by Luis on 29/3/16.
 */
public class AluFlagsExerciseFragment extends BaseExerciseFragment{

    public static AluFlagsExerciseFragment newInstance(){
        AluFlagsExerciseFragment fragment = new AluFlagsExerciseFragment();
        return fragment;
    }

    public AluFlagsExerciseFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el Layout
        View rootView = inflater.inflate(R.layout.fragment_alu_exercise, container, false);
        // Cargamos la imagen de la ALU
        ImageView imageView = (ImageView)rootView.findViewById(R.id.imagealu);
        imageView.setImageResource(R.drawable.alu);

        return rootView;
    }

    @Override
    protected int obtainExerciseId() {
        return R.string.alu;
    }
}
