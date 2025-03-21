import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from '../project.service';
import { Router } from '@angular/router';
import { Modal } from 'bootstrap';
import { TaskService } from './task-detail/task.service';

@Component({
  selector: 'app-project-detail',
  standalone: false,
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css'],
})
export class ProjectDetailComponent implements OnInit {
  project: any;
  newMemberEmail: string = '';
  newMemberRole: string = 'MEMBER';
  message: string = '';
  adminId : any ; 
  selectedTaskId: number | null = null;
  selectedUserId: number | null = null;
  isAdmin: boolean = false;
  userId: string | null = ''; // ID de l'utilisateur connecté
  isMember: boolean = false; 


  newTask = {
    name: '',
    description: '',
    dueDate: '',
    priority: 'MEDIUM',
  };

  constructor(private route: ActivatedRoute, private projectService: ProjectService,private taskService : TaskService,private router: Router) {}

  ngOnInit() {
    const projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.userId = localStorage.getItem("user_id");
    if (!projectId) {
      console.error("ID du projet non trouvé dans l'URL.");
      this.message = "Erreur : ID du projet manquant.";
      return;
    }

    this.loadProjectDetails(projectId);
  }

  loadProjectDetails(projectId: number) {
    this.projectService.getProjectById(projectId).subscribe(
      (data) => {
        if (!data) {
          console.error("Aucun projet trouvé avec cet ID !");
          this.message = "Erreur : projet introuvable.";
          return;
        }

        this.project = data;
        console.log("Projet chargé :", this.project);

        this.checkAdminStatus();
        this.checkMemberStatus();
      },
      (error) => {
        console.error('Erreur lors du chargement du projet', error);
        this.message = "Erreur lors du chargement du projet.";
      }
    );
  }

  addMember() {
    if (!this.project || !this.project.id) {
      console.error("Le projet n'est pas encore chargé !");
      this.message = "Erreur : projet non chargé.";
      return;
    }

    if (!this.newMemberEmail) {
      this.message = "Veuillez entrer une adresse e-mail.";
      return;
    }

    this.projectService.addMemberToProject(this.project.id, this.newMemberEmail, this.newMemberRole).subscribe(
      (response) => {
        this.message = 'Membre ajouté avec succès !';
        this.loadProjectDetails(this.project.id);
        this.newMemberEmail = '';
      },
      (error) => {
        console.error("Erreur lors de l'ajout du membre :", error);
        this.message = 'Erreur lors de l’ajout du membre.';
      }
    );
  }

  createTask() {
    this.adminId = localStorage.getItem("user_id"); 
  if(this.adminId) {

      if (!this.project || !this.project.id) {
        alert("Projet non chargé !");
        return;
      }
      
      if (!this.newTask.name || !this.newTask.description || !this.newTask.dueDate) {
      alert("Veuillez remplir tous les champs !");
      return;
    }
    
    const taskData = {
      userId: this.adminId, // Remplacer par l'ID réel du créateur si nécessaire
      name: this.newTask.name,
      description: this.newTask.description,
      dueDate: this.newTask.dueDate,
      priority: this.newTask.priority,
    };
    
    this.projectService.createTask(this.project.id, taskData).subscribe({
      next: () => {
        alert("Tâche créée avec succès !");
        this.loadProjectDetails(this.project.id);
        this.newTask = { name: '', description: '', dueDate: '', priority: 'MEDIUM' }; // Réinitialisation
      },
      error: (err) => {
        console.error("Erreur :", err);
        alert("Erreur lors de la création de la tâche.");
      },
    });
  }
  }
  
  viewTaskDetails(taskId: number) {
    this.router.navigate(['/task-detail', taskId, this.project.id]);
  }

  getPriorityClass(priority: string) {
    switch (priority) {
      case 'HIGH':
        return 'text-danger';
      case 'MEDIUM':
        return 'text-warning';
      case 'LOW':
        return 'text-success';
      default:
        return '';
    }
  }

  assignTask() {
    if (!this.selectedTaskId || !this.selectedUserId) {
      alert("Veuillez sélectionner un utilisateur !");
      return;
    }

    this.taskService.assignTask(this.selectedTaskId, this.selectedUserId, this.project.id).subscribe({
      next: () => {
        alert("Tâche assignée avec succès !");
        this.loadProjectDetails(this.project.id);
      },
      error: (err) => {
        console.error("Erreur lors de l'assignation :", err);
        alert("Erreur lors de l'assignation !");
      }
    });
  }


  openAssignModal(task: any) {
    this.selectedTaskId = task.id;
    this.selectedUserId = null;
  
    const modalElement = document.getElementById('assignTaskModal') as HTMLElement;
    if (modalElement) {
      const modal = new Modal(modalElement); // ✅ Bootstrap fonctionne maintenant
      modal.show();
    }


  }


  checkAdminStatus() {
    if (!this.project || !this.userId) {
      this.isAdmin = false;
      return;
    }

    // Vérifier si l'utilisateur est l'administrateur créateur du projet
    if (this.project.admin && this.project.admin.id == this.userId) {
      this.isAdmin = true;
      return;
    }

    // Vérifier si l'utilisateur est un admin dans la liste des membres
    const userInProject = this.project.projectMembers.find((member: any) => member.user.id == this.userId);

    if (userInProject && userInProject.role === "ADMIN") {
      this.isAdmin = true;
    } else {
      this.isAdmin = false;
    }

    console.log("isAdmin:", this.isAdmin);
  }


  
  checkMemberStatus() {
    if (!this.project || !this.userId) {
      this.isMember = false;
      return;
    }

    // Vérifier si l'utilisateur est l'administrateur créateur du projet
    if (this.project.admin && this.project.admin.id == this.userId) {
      this.isMember = true;
      return;
    }

    // Vérifier si l'utilisateur est un membre dans la liste des membres
    const userInProject = this.project.projectMembers.find((member: any) => member.user.id == this.userId);

    if (userInProject && userInProject.role === "MEMBER") {
      this.isMember = true;
    } else {
      this.isMember = false;
    }

    console.log("isMember:", this.isMember);
  }
}
