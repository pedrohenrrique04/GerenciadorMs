package view;

import controller.UsuarioController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JFrame {

    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JLabel lblSenha;
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnLogin; // <--- aqui está o botão

    public LoginView() {
        // configurações da tela
        setTitle("login");
        setSize(390, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // painel principal
        JPanel painel = new JPanel();
        painel.setBackground(new Color(245, 245, 245));
        painel.setLayout(null);

        lblTitulo = new JLabel("LOGIN DE USUÁRIO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 33, 33));
        lblTitulo.setSize(230,30);
        int x = (390 - lblTitulo.getWidth()) / 2;
        //int y = (painel.getHeight() - lblTitulo.getHeight()) / 2;
        lblTitulo.setLocation(x,10);
        painel.add(lblTitulo);

        lblUsuario = new JLabel("Usuário:");
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblUsuario.setSize(260,40);
        lblUsuario.setLocation(55,50);
        //lblUsuario.setBounds(65, 60, 260, 40);
        painel.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 60, 160, 25);
        painel.add(txtUsuario);

        lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblSenha.setSize(260,40);
        lblSenha.setLocation(55,85);
        //lblSenha.setBounds(40, 95, 80, 25);
        painel.add(lblSenha);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(120, 95, 160, 25);
        painel.add(txtSenha);

        // botão de login
        btnLogin = new JButton("Login");
        //btnLogin.setBounds(105, 145, 100, 20);
        btnLogin.setSize(100,25);
        btnLogin.setBackground(new Color(0xD6, 0xD6, 0xD6));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        int xB = (390 - btnLogin.getWidth()) / 2;
        btnLogin.setLocation(xB, 135);
        painel.add(btnLogin);

        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(0xE0, 0xE0, 0xE0)); // #e0e0e0
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(new Color(0xD6, 0xD6, 0xD6)); // volta pra cor original
            }
        });

        // cria o controller
        UsuarioController usuarioController = new UsuarioController();

        // ação do botão
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText();
                String senha = new String(txtSenha.getPassword());

                if (usuarioController.autenticar(usuario, senha)) {
                    JOptionPane.showMessageDialog(null, "Login bem-sucedido!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuário ou senha incorretos. reqwerere");
                }
            }
        });

        add(painel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginView();
    }
}
