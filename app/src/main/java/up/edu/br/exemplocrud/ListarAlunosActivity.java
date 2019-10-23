package up.edu.br.exemplocrud;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;

public class ListarAlunosActivity extends AppCompatActivity {

    private ListView listView;
    private AlunoDAO alunoDAO;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alunos);

        listView = findViewById(R.id.listViewAlunos);
        alunoDAO = new AlunoDAO(this);
        alunos = alunoDAO.obterTodos();
        alunosFiltrados.addAll(alunos);
        //ArrayAdapter<Aluno> adaptador = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunosFiltrados);
        AlunoAdapter adaptador = new AlunoAdapter(this, alunosFiltrados);
        listView.setAdapter(adaptador);
        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_principal, menu);

        SearchView sv = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                procuraAluno(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.menu_contexto, menu);
    }

    public void procuraAluno(String nome){
        alunosFiltrados.clear();
        for (Aluno a : alunos) {
            if(a.getNome().toLowerCase().contains(nome.toLowerCase())){
                alunosFiltrados.add(a);
            }
            listView.invalidateViews();
        }
    }

    public void cadastrar(MenuItem item){
        Intent it = new Intent(this, CadastroAlunoActivity.class);
        startActivity(it);
    }

    public void excluir(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Excluir o aluno?")
                .setNegativeButton("NÃO", null)
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alunosFiltrados.remove(alunoExcluir);
                        alunos.remove(alunoExcluir);
                        alunoDAO.excluir((alunoExcluir));
                        listView.invalidateViews();
                    }
                }).create();
        dialog.show();
    }

    public void atualizar(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
        Intent it = new Intent(this, CadastroAlunoActivity.class);
        it.putExtra("aluno", alunoAtualizar);
        startActivity(it);
    }


    @Override
    public void onResume(){
        super.onResume();
        alunos = alunoDAO.obterTodos();
        alunosFiltrados.clear();
        alunosFiltrados.addAll(alunos);
        listView.invalidateViews();
    }

}
