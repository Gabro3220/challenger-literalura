package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DadosLivro;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.model.Results;
import br.com.alura.literalura.repository.iAutorRepository;
import br.com.alura.literalura.repository.iLivrosRepository;
import br.com.alura.literalura.service.ConsumoAPI;
import br.com.alura.literalura.service.ConverteDados;

import java.util.*;

public class Principal {
    private Scanner scan = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private iLivrosRepository livrosRepositorio;
    private iAutorRepository autorRepositorio;
    private static String API_URL = "https://gutendex.com/books/?search=";

    List<Livro> livros;
    List<Autor> autor;

    public Principal(iLivrosRepository livrosRepositorio, iAutorRepository autorRepositorio) {
        this.livrosRepositorio = livrosRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                ┌──────────────────────────────────────────────────────┐
                │                   BEM-VINDO AO LITERALURA            │
                ├──────────────────────────────────────────────────────┤
                │  1 - Buscar livro por nome                           │
                │  2 - Listar livros salvos                            │
                │  3 - Listar autores salvos                           │
                │  4 - Listar autores vivos em um determinado ano      │
                │  5 - Listar livros por idioma                        │
                │                                                      │
                │  0 - Sair                                            │
                └──────────────────────────────────────────────────────┘
                """;
            try {
                System.out.println(menu);
                System.out.print("Escolha uma opção: ");
                opcao = scan.nextInt();
                scan.nextLine(); // Limpa o buffer do scanner
            } catch (InputMismatchException e) {
                System.out.println("┌──────────────────────────────────────────────────────┐");
                System.out.println("│ Por favor, insira um número válido.                  │");
                System.out.println("└──────────────────────────────────────────────────────┘");
                scan.nextLine(); // Limpa o buffer em caso de erro
                continue;
            }

            switch (opcao) {
                case 1:
                    buscarLivro();
                    break;
                case 2:
                    listarLivrosSalvos();
                    break;
                case 3:
                    listarAutoresSalvos();
                    break;
                case 4:
                    listarAutoreVivosEmUmAno();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 0:
                    System.out.println("┌──────────────────────────────────────────────────────┐");
                    System.out.println("│            ENCERRANDO A APLICAÇÃO...                 │");
                    System.out.println("└──────────────────────────────────────────────────────┘\n");
                    break;
                default:
                    System.out.println("┌──────────────────────────────────────────────────────┐");
                    System.out.println("│              OPÇÃO INVÁLIDA! TENTE NOVAMENTE         │");
                    System.out.println("└──────────────────────────────────────────────────────┘\n");
                    break;
            }
        }
    }


    private void listarLivrosPorIdioma() {
        System.out.println("┌──────────────────────────────────────────────────────┐");
        System.out.println("│           LISTA DE LIVROS POR IDIOMA                 │");
        System.out.println("└──────────────────────────────────────────────────────┘\n");
        System.out.println("""
            ---- Escolha o idioma ----
            en - Inglês
            es - Espanhol
            fr - Francês
            pt - Português
            """);
        String idioma = scan.nextLine();
        livros = livrosRepositorio.findByIdiomasContains(idioma);
        if (livros.isEmpty()) {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│      Nenhum livro encontrado no idioma escolhido.    │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            listarLivrosPorIdioma();
        } else {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│         LIVROS NO IDIOMA ESCOLHIDO                   │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            livros.stream()
                    .sorted(Comparator.comparing(Livro::getTitulo))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoreVivosEmUmAno() {
        System.out.println("┌──────────────────────────────────────────────────────┐");
        System.out.println("│   LISTA DE AUTORES VIVOS EM UM DETERMINADO ANO       │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        System.out.print("Por favor, insira o ano: ");
        Integer ano = Integer.valueOf(scan.nextLine());
        autor = autorRepositorio
                .findByAnoNascimentoLessThanEqualAndAnoFalecimentoGreaterThanEqual(ano, ano);
        if (autor.isEmpty()) {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│         Nenhum autor encontrado para o ano           │");
            System.out.println("└──────────────────────────────────────────────────────┘");
        } else {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│         AUTORES VIVOS NO ANO ESCOLHIDO               │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            autor.stream()
                    .sorted(Comparator.comparing(Autor::getNome))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresSalvos() {
        System.out.println("┌──────────────────────────────────────────────────────┐");
        System.out.println("│           LISTA DE AUTORES NO BANCO DE DADOS         │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        autor = autorRepositorio.findAll();
        autor.stream()
                .sorted(Comparator.comparing(Autor::getNome))
                .forEach(System.out::println);
    }

    private void listarLivrosSalvos() {
        System.out.println("┌──────────────────────────────────────────────────────┐");
        System.out.println("│           LISTA DE LIVROS NO BANCO DE DADOS          │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        livros = livrosRepositorio.findAll();
        livros.stream()
                .sorted(Comparator.comparing(Livro::getTitulo))
                .forEach(System.out::println);
    }

    private void buscarLivro() {
        System.out.println("┌──────────────────────────────────────────────────────┐");
        System.out.println("│                  BUSCAR LIVRO                        │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        System.out.print("Qual livro deseja buscar?: ");
        var nomeLivro = scan.nextLine().toLowerCase();
        var json = consumo.obterDados(API_URL + nomeLivro.replace(" ", "%20").trim());
        var dados = conversor.obterDados(json, Results.class);
        if (dados.results().isEmpty()) {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│              Livro não encontrado!                   │");
            System.out.println("└──────────────────────────────────────────────────────┘");
        } else {
            DadosLivro dadosLivro = dados.results().get(0);
            Livro livro = new Livro(dadosLivro);
            Autor autor = new Autor().pegaAutor(dadosLivro);
            salvarDados(livro, autor);
        }
    }

    private void salvarDados(Livro livro, Autor autor) {
        Optional<Livro> livroEncontrado = livrosRepositorio.findByTituloContains(livro.getTitulo());
        if (livroEncontrado.isPresent()) {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│         Esse livro já existe no banco de dados       │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            System.out.println(livro.toString());
        } else {
            try {
                livrosRepositorio.save(livro);
                System.out.println("┌──────────────────────────────────────────────────────┐");
                System.out.println("│               Livro guardado com sucesso             │");
                System.out.println("└──────────────────────────────────────────────────────┘");
                System.out.println(livro);
            } catch (Exception e) {
                System.out.println("Erro ao salvar livro: " + e.getMessage());
            }
        }

        Optional<Autor> autorEncontrado = autorRepositorio.findByNomeContains(autor.getNome());
        if (autorEncontrado.isPresent()) {
            System.out.println("┌──────────────────────────────────────────────────────┐");
            System.out.println("│         Esse autor já existe no banco de dados       │");
            System.out.println("└──────────────────────────────────────────────────────┘");
            System.out.println(autor.toString());
        } else {
            try {
                autorRepositorio.save(autor);
                System.out.println("┌──────────────────────────────────────────────────────┐");
                System.out.println("│               Autor guardado com sucesso             │");
                System.out.println("└──────────────────────────────────────────────────────┘");
                System.out.println(autor);
            } catch (Exception e) {
                System.out.println("Erro ao salvar autor: " + e.getMessage());
            }
        }
    }


}
