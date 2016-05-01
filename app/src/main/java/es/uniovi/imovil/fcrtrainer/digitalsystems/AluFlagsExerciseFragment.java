package es.uniovi.imovil.fcrtrainer.digitalsystems;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import es.uniovi.imovil.fcrtrainer.BaseExerciseFragment;
import es.uniovi.imovil.fcrtrainer.R;
import es.uniovi.imovil.fcrtrainer.digitalinformation.BinaryConverter;

/**
 * Created by Luis on 29/3/16.
 */
public class AluFlagsExerciseFragment extends BaseExerciseFragment{

    private static final int N_BITS = 4;
    private static final int FLAG_SIZE = 2;

    private Random mRandomGenerator = new Random();
    private int randomGenerated;
    private int minNumber;
    private int maxNumber;

    private View mRootView;
    private TextView mOperando1;
    private TextView mOperando2;
    private TextView mOp0;
    private TextView mOp1;
    private TextView mCarry_in;
    private TextView mCompl_1;
    private TextView mResult;
    private TextView mZcos;
    private Button cButton;
    private Button sButton;


    private boolean zflag = false;
    private boolean cflag = false;
    private boolean oflag = false;
    private boolean sflag = false;
    private String resultFlags="";

    private int mEntrada1;
    private int mEntrada2;
    private int result = 0;


    public static AluFlagsExerciseFragment newInstance(){
        AluFlagsExerciseFragment fragment = new AluFlagsExerciseFragment();
        return fragment;
    }

    public AluFlagsExerciseFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos el Layout
        mRootView = inflater.inflate(R.layout.fragment_alu_exercise, container, false);
        // Cargamos la imagen de la ALU
        ImageView imageView = (ImageView)mRootView.findViewById(R.id.imagealu);
        imageView.setImageResource(R.drawable.alu);
        Button sButton = (Button)mRootView.findViewById(R.id.sButton);
        cButton = (Button)mRootView.findViewById(R.id.cButton);

        inicializaEjercicio();
        resuelveFlags();

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSolution();
            }
        });

        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSolution();
            }
        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mIsPlaying) {
            sButton.setVisibility(View.GONE);
        }
    }

    /* Inicializamos las variables iniciales del enunciado, operandos
       y entradas que definiran laoperacion aritmetica o logica que realizara la ALU */
    public void inicializaEjercicio(){
        mOperando1 = (TextView)mRootView.findViewById(R.id.operando1);
        mOperando2 = (TextView)mRootView.findViewById(R.id.operando2);
        mOp0 = (TextView)mRootView.findViewById(R.id.op0);
        mOp1 = (TextView)mRootView.findViewById(R.id.op1);
        mCarry_in = (TextView)mRootView.findViewById(R.id.carry_in);
        mCompl_1 = (TextView)mRootView.findViewById(R.id.compl_1);
        mResult = (TextView)mRootView.findViewById(R.id.r);
        mZcos = (TextView)mRootView.findViewById(R.id.zcos);

        mOperando1.setText(generateRandomBinaryNumber(N_BITS));
        mEntrada1 = randomGenerated;
        mOperando2.setText(generateRandomBinaryNumber(N_BITS));
        mEntrada2 = randomGenerated;
        mOp0.setText(generateRandomBinaryNumber(FLAG_SIZE));
        mOp1.setText(generateRandomBinaryNumber(FLAG_SIZE));
        mCarry_in.setText(generateRandomBinaryNumber(FLAG_SIZE));
        mCompl_1.setText(generateRandomBinaryNumber(FLAG_SIZE));
        mResult.setText(calculateResult());
    }

    // Genera numeros binarios aleatorios según los N bits de entrada
    public String generateRandomBinaryNumber(int numberOfBits){
        int randomNumber;

        // Genera los operandos 1 y 2
        if(numberOfBits>FLAG_SIZE) {
            minNumber = (int) -(Math.pow(2, N_BITS - 1));
            maxNumber = (int) (Math.pow(2, N_BITS - 1)) - 1;

            randomNumber = mRandomGenerator.nextInt(maxNumber - minNumber + 1) + minNumber;
            randomGenerated = randomNumber;
            Log.e("operando", "" + randomGenerated);
            return BinaryConverter.binaryToStringWithNbits(randomNumber, numberOfBits);
        }

        // Genera los flags
        else{
            randomNumber = mRandomGenerator.nextInt(FLAG_SIZE);
            Log.e("flag", "" + randomNumber);
            return Integer.toBinaryString(randomNumber);
        }

    }

    /* Resolvemos la operación, no es un requisito pero ayuda al usuario
       a resolver los flags */
    public String calculateResult(){
        Log.e("entrada1: ", "" + mEntrada1);
        Log.e("entrada2: ", "" + mEntrada2);

        Log.e("operacion", "" + getOperation());
        switch(getOperation()){
            case "ADD":
                result = mEntrada1 + mEntrada2;
                if(result > maxNumber) {
                    result = 7;
                    // C flag
                    cflag = true;
                }
                break;

            case "SUB":
                result = mEntrada1 - mEntrada2;
                if(result < minNumber){
                    result = -8;
                    cflag = true;
                }
                break;

            case "AND":
                result = mEntrada1 & mEntrada2;
                break;

            case "OR":
                result = mEntrada1 | mEntrada2;
                break;

            case "XOR":
                result = mEntrada1 ^ mEntrada2;
                break;
        }


        Log.e("resultado", ""+result);

        return BinaryConverter.binaryToStringWithNbits(result, N_BITS);
    }

    public String getOperation(){
        boolean op0 = "1".equals(mOp0.getText().toString());
        boolean op1 = "1".equals(mOp1.getText().toString());
        boolean carry_in = "1".equals(mCarry_in.getText().toString());
        boolean compl_1 = "1".equals(mCompl_1.getText().toString());

        if(!op1 && !op0)
            return "AND";
        else if(!op1 && op0)
            return "OR";
        else if(op1 && !op0)
            return "XOR";
        else{
            if(compl_1)
                return "SUB";

            return "ADD";
        }
    }

    public void resuelveFlags(){
        // Z flag
        if(mResult.getText().toString().equals("0000"))
            zflag = true;

        // O flag
        oflag = cflag;

        // S flag
        if(result<0){
            sflag = true;
        }

        resultFlags = "" + booleanToBinary(zflag) + booleanToBinary(cflag) +
                booleanToBinary(oflag) + booleanToBinary(sflag);

    }

    public String booleanToBinary(boolean flag){
        if (flag == true)
                return "1";
        return "0";
    }

    @Override
    protected int obtainExerciseId() {
        return R.string.alu;
    }

    private void showSolution(){
        mZcos.setText(resultFlags);
    }

    private void checkSolution(){
        String answer = mZcos.getEditableText().toString();
        Log.e("solucion", "solucion: " + answer);
        cButton.setVisibility(View.VISIBLE);
        if(!isCorrect(answer)){
            showAnimationAnswer(false);
            computeIncorrectQuestion();
        }
        else{
            showAnimationAnswer(true);
            if (mIsPlaying) {
                computeCorrectQuestion();
            }
            // newQuestion()
        }

    }

    private boolean isCorrect(String answer){
        return answer.equals(resultFlags);
    }
}
