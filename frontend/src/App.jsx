import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastProvider } from './components/Toast';
import Layout from './components/Layout';
import { useAuth } from './context/AuthContext';
import Dashboard from './pages/Dashboard';
import LoginPage from './pages/Auth/LoginPage';
import RegisterPage from './pages/Auth/RegisterPage';
import ForgotPasswordPage from './pages/Auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/Auth/ResetPasswordPage';
import EmailVerifyPage from './pages/Auth/EmailVerifyPage';
import EtudiantList from './pages/Etudiants/EtudiantList';
import EtudiantForm from './pages/Etudiants/EtudiantForm';
import ProfesseurList from './pages/Professeurs/ProfesseurList';
import ProfesseurForm from './pages/Professeurs/ProfesseurForm';
import CoursList from './pages/Cours/CoursList';
import CoursForm from './pages/Cours/CoursForm';
import SalleList from './pages/Salles/SalleList';
import SalleForm from './pages/Salles/SalleForm';
import InscriptionList from './pages/Inscriptions/InscriptionList';
import InscriptionForm from './pages/Inscriptions/InscriptionForm';
import NoteList from './pages/Notes/NoteList';
import NoteForm from './pages/Notes/NoteForm';
import GradeList from './pages/Grades/GradeList';
import GradeForm from './pages/Grades/GradeForm';
import PaiementList from './pages/Paiements/PaiementList';
import PaiementForm from './pages/Paiements/PaiementForm';
import EmploiDuTempsList from './pages/EmploiDuTemps/EmploiDuTempsList';
import EmploiDuTempsForm from './pages/EmploiDuTemps/EmploiDuTempsForm';
import FichierList from './pages/Fichiers/FichierList';
import PresenceList from './pages/Presences/PresenceList';
import PresenceForm from './pages/Presences/PresenceForm';
import PresenceAppel from './pages/Presences/PresenceAppel';
import ProfilePage from './pages/Profile/ProfilePage';
import AdminUsers from './pages/Admin/AdminUsers';
import AdminUserForm from './pages/Admin/AdminUserForm';
import AuditLogs from './pages/Admin/AuditLogs';

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading">Chargement...</div>;
  return user ? children : <Navigate to="/login" replace />;
}

function PublicRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading">Chargement...</div>;
  return user ? <Navigate to="/" replace /> : children;
}

export default function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
      <Routes>
        <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
        <Route path="/forgot-password" element={<PublicRoute><ForgotPasswordPage /></PublicRoute>} />
        <Route path="/reset-password" element={<PublicRoute><ResetPasswordPage /></PublicRoute>} />
        <Route path="/verify" element={<PublicRoute><EmailVerifyPage /></PublicRoute>} />
        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/etudiants" element={<EtudiantList />} />
          <Route path="/etudiants/nouveau" element={<EtudiantForm />} />
          <Route path="/etudiants/:id" element={<EtudiantForm />} />
          <Route path="/professeurs" element={<ProfesseurList />} />
          <Route path="/professeurs/nouveau" element={<ProfesseurForm />} />
          <Route path="/professeurs/:id" element={<ProfesseurForm />} />
          <Route path="/cours" element={<CoursList />} />
          <Route path="/cours/nouveau" element={<CoursForm />} />
          <Route path="/cours/:id" element={<CoursForm />} />
          <Route path="/salles" element={<SalleList />} />
          <Route path="/salles/nouveau" element={<SalleForm />} />
          <Route path="/salles/:id" element={<SalleForm />} />
          <Route path="/inscriptions" element={<InscriptionList />} />
          <Route path="/inscriptions/nouveau" element={<InscriptionForm />} />
          <Route path="/inscriptions/:id" element={<InscriptionForm />} />
          <Route path="/notes" element={<NoteList />} />
          <Route path="/notes/nouveau" element={<NoteForm />} />
          <Route path="/notes/:id" element={<NoteForm />} />
          <Route path="/grades" element={<GradeList />} />
          <Route path="/grades/nouveau" element={<GradeForm />} />
          <Route path="/grades/:id" element={<GradeForm />} />
          <Route path="/paiements" element={<PaiementList />} />
          <Route path="/paiements/nouveau" element={<PaiementForm />} />
          <Route path="/paiements/:id" element={<PaiementForm />} />
          <Route path="/emploi-du-temps" element={<EmploiDuTempsList />} />
          <Route path="/emploi-du-temps/nouveau" element={<EmploiDuTempsForm />} />
          <Route path="/emploi-du-temps/:id" element={<EmploiDuTempsForm />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/fichiers" element={<FichierList />} />
          <Route path="/presences" element={<PresenceList />} />
          <Route path="/presences/nouveau" element={<PresenceForm />} />
          <Route path="/presences/appel" element={<PresenceAppel />} />
          <Route path="/presences/:id" element={<PresenceForm />} />
          <Route path="/admin/utilisateurs" element={<AdminUsers />} />
          <Route path="/admin/utilisateurs/nouveau" element={<AdminUserForm />} />
          <Route path="/admin/utilisateurs/:id" element={<AdminUserForm />} />
          <Route path="/admin/audit-logs" element={<AuditLogs />} />
        </Route>
      </Routes>
      </ToastProvider>
    </BrowserRouter>
  );
}
