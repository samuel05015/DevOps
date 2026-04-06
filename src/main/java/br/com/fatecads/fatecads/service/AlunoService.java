package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.repository.AlunoRepository;

@Service
public class AlunoService {
    
    //injeção de dependência do repositório de aluno
    @Autowired
    private AlunoRepository alunoRepository;

    //método para salvar um aluno
    public Aluno save(Aluno aluno){
    return alunoRepository.save(aluno);
    }

    //Método para listar todos alunos

    public List<Aluno> findAll(){
      return alunoRepository.findAll();  
    }

    //Método para excluir um aluno por id
    public void deleteById(Integer id){
        alunoRepository.deleteById(id);
    }

    //Método para buscar um aluno por id
    public Aluno findById(Integer id){
        return alunoRepository.findById(id).orElse(null);
    }




}
