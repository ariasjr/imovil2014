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

public class AluFlagsExerciseFragment extends BaseExerciseFragment{

    private static final int N_BITS = 4;
    private static final int FLAG_SIZE = 2;
    private static final String STATE_OPERANDO1 = "mOperando1";
    private static final String STATE_OPERANDO2 = "mOperando2";
    private static final String STATE_OP0 = "mOp0";
    private static final String STATE_OP1 = "mOp1";
    private static final String STATE_CARRY_IN = "mCarry_in";
    private static final String STATE_COMPL_1 = "mCompl_1";
    private static final String STATE_RESULT = "mResult";
    private static final String STATE_ZCOS = "mZcos";
    private static final String STATE_ZCOS_RESULT = "resultFlags";

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

        // Inicializamos los componentes del layout
        sButton = (Button)mRootView.findViewById(R.id.sButton);
        cButton = (Button)mRootView.findViewById(R.id.cButton);
        mOperando1 = (TextView)mRootView.findViewById(R.id.operando1);
        mOperando2 = (TextView)mRootView.findViewById(R.id.operando2);
        mOp0 = (TextView)mRootView.findViewById(R.id.op0);
        mOp1 = (TextView)mRootView.findViewById(R.id.op1);
        mCarry_in = (TextView)mRootView.findViewById(R.id.carry_in);
        mCompl_1 = (TextView)mRootView.findViewById(R.id.compl_1);
        mResult = (TextView)mRootView.findViewById(R.id.r);
        mZcos = (TextView)mRootView.findViewById(R.id.zcos);

        if(savedInstanceState==null){
            // Generamos un nuevo ejercicio
            inicializaEjercicio();
            resuelveFlags();
        }
        else{
            // Recuperamos los datos del ejercicio en curso
            mOperando1.setText(savedInstanceState.getString(STATE_OPERANDO1));
            mOperando2.setText(savedInstanceState.getString(STATE_OPERANDO2));
            mOp0.setText(savedInstanceState.getString(STATE_OP0));
            mOp1.setText(savedInstanceState.getString(STATE_OP1));
            mCarry_in.setText(savedInstanceState.getString(STATE_CARRY_IN));
            mCompl_1.setText(savedInstanceState.getString(STATE_COMPL_1));
            mResult.setText(savedInstanceState.getString(STATE_RESULT));
            mZcos.setText(savedInstanceState.getString(STATE_ZCOS));
        }

        // Eventos de los buttons
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

        // Si está en modo "juego" no hay botón de solución
        if (mIsPlaying) {
            sButton.setVisibility(View.GONE);
        }

        // Recuperamos la información introducida por el usuario
        if(savedInstanceState != null){
            resultFlags = savedInstanceState.getString(STATE_ZCOS_RESULT);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Almacenamos todas las instancias de las variables
        outState.putString(STATE_OPERANDO1, mOperando1.getText().toString());
        outState.putString(STATE_OPERANDO2, mOperando2.getText().toString());
        outState.putString(STATE_OP0, mOp0.getText().toString());
        outState.putString(STATE_OP1, mOp1.getText().toString());
        outState.putString(STATE_CARRY_IN, mCarry_in.getText().toString());
        outState.putString(STATE_COMPL_1, mCompl_1.getText().toString());
        outState.putString(STATE_RESULT, mResult.getText().toString());
        outState.putString(STATE_ZCOS, mZcos.getText().toString());
        outState.putString(STATE_ZCOS_RESULT, resultFlags);
    }


    /* Inicializamos las variables iniciales del enunciado, operandos
     * y entradas que definiran la operacion aritmetica o logica que realizara la ALU */
    public void inicializaEjercicio(){
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

    /* Genera numeros binarios aleatorios según los N bits de entrada.
     * Retorna flags o números binarios en complemento a 2 */
    public String generateRandomBinaryNumber(int numberOfBits){
        int randomNumber;

        // Genera los operandos 1 y 2 si el tamaño del número a generar
        // es mayor que el tamaño de un flag
        if(numberOfBits>FLAG_SIZE) {
            minNumber = (int) -(Math.pow(2, N_BITS - 1));
            maxNumber = (int) (Math.pow(2, N_BITS - 1)) - 1;

            randomNumber = mRandomGenerator.nextInt(maxNumber - minNumber + 1) + minNumber;
            randomGenerated = randomNumber;
            Log.e("Operando: ", "" + randomGenerated);
            return BinaryConverter.binaryToStringWithNbits(randomNumber, numberOfBits);
        }

        // Sino genera los flags
        else{
            randomNumber = mRandomGenerator.nextInt(FLAG_SIZE);
            Log.e("Flag: ", "" + randomNumber);
            return Integer.toBinaryString(randomNumber);
        }

    }

    /* Resolvemos la operación, no es un requisito pero ayuda al usuario
     * a resolver los flags de estado */
    public String calculateResult(){
        Log.e("Entrada1: ", "" + mEntrada1);
        Log.e("Entrada2: ", "" + mEntrada2);
        // Si el flag de carry_in = 1 debemos sumar +1 al resultado
        boolean carry_in = "1".equals(mCarry_in.getText().toString());

        // Calculamos el resultado
        Log.e("Operacion", "" + getOperation());
        switch(getOperation()){
            case "ADD":
                result = mEntrada1 + mEntrada2;
                if(result > maxNumber) {
                    // Overflow flag: cuando el resultado desborda
                    // el rango de representación
                    oflag = true;
                }
                if(carry_in)
                    result+=1;
                break;

            case "SUB":
                result = mEntrada1 - mEntrada2;
                if(result < minNumber)
                    oflag = true;
                // Carry flag: cuando el minuendo es menor que
                // el sustraendo
                if(mEntrada1<mEntrada2)
                    cflag = true;
                if(carry_in)
                    result+=1;
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


        Log.e("Resultado: ", "" + result);
        return BinaryConverter.binaryToStringWithNbits(result, N_BITS);
    }

    /* Obtiene el operando dependiendo de los flags de entrada generados */
    public String getOperation(){
        boolean op0 = "1".equals(mOp0.getText().toString());
        boolean op1 = "1".equals(mOp1.getText().toString());
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

    /* Calcula el resultado de los flags ZCOS */
    public void resuelveFlags(){
        // Z flag: Cuando el resultado es 0
        if(mResult.getText().toString().equals("0000"))
            zflag = true;

        // S flag: Cuando el resultado es negativo
        if(result<0){
            sflag = true;
        }
        // Mostramos el resultado como la concatenación de los flags
        // previamente calculados
        resultFlags = "" + booleanToBinary(zflag) + booleanToBinary(cflag) +
                booleanToBinary(oflag) + booleanToBinary(sflag);

    }

    /* Convierte un booleano a un String binario*/
    public String booleanToBinary(boolean flag){
        if (flag)
                return "1";
        return "0";
    }

    @Override
    protected int obtainExerciseId() {
        return R.string.alu;
    }

    /* Muestra la solución ZCOS */
    private void showSolution(){
        mZcos.setText(resultFlags);
    }

    /* Corrige el input del usuario con la solución calculada */
    private void checkSolution(){
        String answer = mZcos.getEditableText().toString();

        if(!isCorrect(answer)){
            showAnimationAnswer(false);

            // Si estamos en modo "juego" respuesta correcta
            if (mIsPlaying)
                computeCorrectQuestion();
        }
        else{
            showAnimationAnswer(true);
            // Si estamos en modo "juego" respuesta incorrecta
            if (mIsPlaying) {
                computeIncorrectQuestion();
            }
            // Y generamos otra nuevo ejercicio
            newQuestion();
        }

    }

    private boolean isCorrect(String answer){
        return answer.equals(resultFlags);
    }

    /* Genera un nuevo ejercicio */
    private void newQuestion(){
        mZcos.setText("");
        cleanFlags();
        inicializaEjercicio();
        resuelveFlags();
    }

    /* Reinicializa los flags de estado actuales*/
    private void cleanFlags(){
        zflag = false;
        cflag = false;
        oflag = false;
        sflag = false;
    }

    @Override
    protected void startGame() {
        super.startGame();

        // Cambiamos el layout y se adapta al modo juego
        sButton.setVisibility(View.GONE);
        newQuestion();
    }

    @Override
    protected void cancelGame() {
        super.cancelGame();

        sButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void endGame() {
        super.endGame();

        // Cambiamos el layout para dejarlo en modo ejercicio
        sButton.setVisibility(View.VISIBLE);
    }
}
