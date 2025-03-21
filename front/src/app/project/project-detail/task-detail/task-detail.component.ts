import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { TaskService } from './task.service';
import { Modal } from 'bootstrap';
import { ProjectService } from '../../project.service';

@Component({
  selector: 'app-task-detail',
  standalone: false,
  templateUrl: './task-detail.component.html'
})
export class TaskDetailComponent implements OnInit {
  task: any = {};
  editTask: any = {}; // Objet temporaire pour l'édition
  taskId: number | null = null;
  projectId: number | null = null;
  history: any[] = []; // Stocker l'historique des modifications
  userId: string | null = ''; // ID de l'utilisateur connecté
  isMember: boolean = false; 
  isAdmin: boolean = false;
  project: any;


  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private taskService: TaskService,
    private projectService: ProjectService
  ) {}

  ngOnInit() {
    this.userId = localStorage.getItem("user_id"); 
    this.route.paramMap.subscribe(params => {
      this.taskId = Number(params.get('id'));
      this.projectId = Number(params.get('projectId'));
      if (this.taskId && this.projectId) {
        this.getTaskDetails(this.taskId, this.projectId);
        this.getTaskHistory(this.taskId, this.projectId); // Charger l'historique
        this.loadProjectDetails(this.projectId);
      }
    });
  }

  getTaskDetails(taskId: number, projectId: number) {
    this.http.get(`http://localhost:8080/api/projects/${projectId}/tasks/${taskId}`)
      .subscribe((data: any) => { 
        this.task = { ...data, project_id: projectId };
      });
  }

  getTaskHistory(taskId: number, projectId: number) {
    this.http.get(`http://localhost:8080/api/projects/${projectId}/tasks/${taskId}/history`)
      .subscribe((data: any) => { 
        this.history = data; 
      });
  }

  openEditModal() {
    this.editTask = { ...this.task }; // Copier les infos actuelles dans l'objet d'édition
    const modal = new Modal(document.getElementById('editTaskModal')!);
    modal.show();
  }

  updateTask() {
    if (!this.taskId || !this.projectId || !this.userId) return;
  
    const updates: any = {
      name: this.editTask.name,
      description: this.editTask.description,
      dueDate: this.editTask.dueDate,
      priority: this.editTask.priority,
      status: this.editTask.status
    };
  
    const userIdNumber = Number(this.userId); // ✅ Convertir en number
  
    this.taskService.updateTask(this.projectId, this.taskId, updates, userIdNumber)
      .subscribe(() => {
        this.getTaskDetails(this.taskId!, this.projectId!);
        this.getTaskHistory(this.taskId!, this.projectId!);
        alert('Tâche mise à jour avec succès !');
        const modal = Modal.getInstance(document.getElementById('editTaskModal')!);
        modal?.hide();
      }, error => {
        console.error("Erreur lors de la mise à jour de la tâche", error);
      });
  }
  

  getPriorityClass(priority: string) {
    switch (priority) {
      case 'HIGH': return 'text-danger';
      case 'MEDIUM': return 'text-warning';
      case 'LOW': return 'text-success';
      default: return '';
    }
  }

  
  loadProjectDetails(projectId: number) {
    this.projectService.getProjectById(projectId).subscribe(
      (data) => {
        if (!data) {
          console.error("Aucun projet trouvé avec cet ID !");
          return;
        }

        this.project = data;
        console.log("Projet chargé :", this.project);

        this.checkAdminStatus();
        this.checkMemberStatus();
      },
      (error) => {
        console.error('Erreur lors du chargement du projet', error);
      }
    );
  }

  checkAdminStatus() {
    console.log("checkAdminStatus appellé")
    if (!this.project || !this.userId) {
      this.isAdmin = false;
      console.log("isAdmin1:", this.isAdmin);
      return;
    }

    // Vérifier si l'utilisateur est l'administrateur créateur du projet
    if (this.project.admin && this.project.admin.id == this.userId) {
      this.isAdmin = true;
      console.log("isAdmin2:", this.isAdmin);
      return;
    }

    // Vérifier si l'utilisateur est un admin dans la liste des membres
    const userInProject = this.project.projectMembers.find((member: any) => member.user.id == this.userId);

    if (userInProject && userInProject.role === "ADMIN") {
      this.isAdmin = true;
    } else {
      this.isAdmin = false;
    }

    console.log("isAdmin3:", this.isAdmin);
  }
  checkMemberStatus() {
    console.log("checkMemberStatus appellé")
    if (!this.project || !this.userId) {
      this.isMember = false;
      console.log("isMember1:", this.isMember);
      return;
    }

    // Vérifier si l'utilisateur est l'administrateur créateur du projet
    if (this.project.admin && this.project.admin.id == this.userId) {
      this.isMember = true;
      console.log("isMember2:", this.isMember);
      return;
    }

    // Vérifier si l'utilisateur est un membre dans la liste des membres
    const userInProject = this.project.projectMembers.find((member: any) => member.user.id == this.userId);

    if (userInProject && userInProject.role === "MEMBER") {
      this.isMember = true;
    } else {
      this.isMember = false;
    }

    console.log("isMember3:", this.isMember);
  }
}
