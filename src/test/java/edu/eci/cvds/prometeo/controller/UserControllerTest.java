// package edu.eci.cvds.prometeo.controller;
//
// import edu.eci.cvds.prometeo.model.*;
// import edu.eci.cvds.prometeo.repository.RoutineExerciseRepository;
// import edu.eci.cvds.prometeo.repository.RoutineRepository;
// import edu.eci.cvds.prometeo.service.*;
// import edu.eci.cvds.prometeo.dto.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.*;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
// @ExtendWith(MockitoExtension.class)
// public class UserControllerTest {
//
//     private MockMvc mockMvc;
//
//     @Mock
//     private UserService userService;
//
//     @Mock
//     private GymReservationService gymReservationService;
//
//     @Mock
//     private RoutineRepository routineRepository;
//
//     @Mock
//     private RoutineExerciseRepository routineExerciseRepository;
//
//     @Mock
//     private BaseExerciseService baseExerciseService;
//
//     @Mock
//     private GoalService goalService;
//
//     @Mock
//     private GymSessionService gymSessionService;
//
//     @InjectMocks
//     private UserController userController;
//
//     private ObjectMapper objectMapper;
//
//     @BeforeEach
//     void setUp() {
//         mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//         objectMapper = new ObjectMapper();
//         objectMapper.registerModule(new JavaTimeModule());
//     }

//     @Test
//     void getUserById_ShouldReturnUser() {
//         String userId = "123e4567-e89b-12d3-a456-426614174000";
//         User mockUser = new User();
//         mockUser.setId(UUID.fromString(userId));
//         mockUser.setName("Test User");
//         mockUser.setEmail("test@example.com");
//
//         when(userService.getUserById(userId)).thenReturn(mockUser);
//
//         ResponseEntity<User> response = userController.getUserById(userId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(userId, response.getBody().getId().toString());
//         assertEquals("Test User", response.getBody().getName());
//         verify(userService).getUserById(userId);
//     }
//
//     @Test
//     void getUserByInstitutionalId_ShouldReturnUser() {
//         String institutionalId = "12345";
//         User mockUser = new User();
//         mockUser.setId(UUID.randomUUID());
//         mockUser.setName("Test User");
//         mockUser.setInstitutionalId(institutionalId);
//
//         when(userService.getUserByInstitutionalId(institutionalId)).thenReturn(mockUser);
//
//         ResponseEntity<User> response = userController.getUserByInstitutionalId(institutionalId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(institutionalId, response.getBody().getInstitutionalId());
//         verify(userService).getUserByInstitutionalId(institutionalId);
//     }
//
//     @Test
//     void getAllUsers_ShouldReturnListOfUsers() {
//         List<User> mockUsers = Arrays.asList(
//                 createMockUser("User 1"),
//                 createMockUser("User 2")
//         );
//
//         when(userService.getAllUsers()).thenReturn(mockUsers);
//
//         ResponseEntity<List<User>> response = userController.getAllUsers();
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         verify(userService).getAllUsers();
//     }
//
//     @Test
//     void getUsersByRole_ShouldReturnFilteredUsers() {
//         String role = "TRAINER";
//         List<User> mockTrainers = Arrays.asList(
//                 createMockUser("Trainer 1"),
//                 createMockUser("Trainer 2")
//         );
//
//         when(userService.getUsersByRole(role)).thenReturn(mockTrainers);
//
//         ResponseEntity<List<User>> response = userController.getUsersByRole(role);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         verify(userService).getUsersByRole(role);
//     }
//
//     @Test
//     void updateUser_ShouldReturnUpdatedUser() {
//         String userId = "123e4567-e89b-12d3-a456-426614174000";
//         UserDTO userDTO = new UserDTO();
//         userDTO.setName("Updated User");
//         userDTO.setEmail("updated@example.com");
//
//         User updatedUser = new User();
//         updatedUser.setId(UUID.fromString(userId));
//         updatedUser.setName("Updated User");
//         updatedUser.setEmail("updated@example.com");
//
//         when(userService.updateUser(eq(userId), any(UserDTO.class))).thenReturn(updatedUser);
//
//         ResponseEntity<User> response = userController.updateUser(userId, userDTO);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Updated User", response.getBody().getName());
//         assertEquals("updated@example.com", response.getBody().getEmail());
//         verify(userService).updateUser(eq(userId), any(UserDTO.class));
//     }
//
//     @Test
//     void createUser_ShouldReturnCreatedUser() {
//         UserDTO userDTO = new UserDTO();
//         userDTO.setName("New User");
//         userDTO.setEmail("new@example.com");
//
//         User createdUser = new User();
//         createdUser.setId(UUID.randomUUID());
//         createdUser.setName("New User");
//         createdUser.setEmail("new@example.com");
//
//         when(userService.createUser(any(UserDTO.class))).thenReturn(createdUser);
//
//         ResponseEntity<User> response = userController.createUser(userDTO);
//
//         assertEquals(HttpStatus.CREATED, response.getStatusCode());
//         assertEquals("New User", response.getBody().getName());
//         assertEquals("new@example.com", response.getBody().getEmail());
//         verify(userService).createUser(any(UserDTO.class));
//     }
//
//     @Test
//     void deleteUser_ShouldReturnDeletedUser() {
//         String institutionalId = "12345";
//         User deletedUser = new User();
//         deletedUser.setId(UUID.randomUUID());
//         deletedUser.setName("Deleted User");
//         deletedUser.setInstitutionalId(institutionalId);
//
//         when(userService.deleteUser(institutionalId)).thenReturn(deletedUser);
//
//         ResponseEntity<User> response = userController.deleteUser(institutionalId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Deleted User", response.getBody().getName());
//         verify(userService).deleteUser(institutionalId);
//     }
//
//     @Test
//     void recordPhysicalMeasurement_ShouldReturnSavedProgress() {
//         UUID userId = UUID.randomUUID();
//         PhysicalProgressDTO progressDTO = createMockPhysicalProgressDTO();
//
//         PhysicalProgress savedProgress = new PhysicalProgress();
//         savedProgress.setId(UUID.randomUUID());
//         savedProgress.setWeight(new Weight(70.0, Weight.WeightUnit.KG));
//         savedProgress.setMeasurements(new BodyMeasurements());
//         savedProgress.setPhysicalGoal("Gain muscle");
//
//         when(userService.recordPhysicalMeasurement(eq(userId), any(PhysicalProgress.class)))
//                 .thenReturn(savedProgress);
//
//         ResponseEntity<PhysicalProgress> response = userController.recordPhysicalMeasurement(userId, progressDTO);
//
//         assertEquals(HttpStatus.CREATED, response.getStatusCode());
//         assertNotNull(response.getBody());
//         assertEquals("Gain muscle", response.getBody().getPhysicalGoal());
//         verify(userService).recordPhysicalMeasurement(eq(userId), any(PhysicalProgress.class));
//     }
//
//     @Test
//     void getPhysicalMeasurementHistory_ShouldReturnHistory() {
//         UUID userId = UUID.randomUUID();
//         LocalDate startDate = LocalDate.now().minusMonths(1);
//         LocalDate endDate = LocalDate.now();
//
//         List<PhysicalProgress> mockHistory = Arrays.asList(
//                 createMockPhysicalProgress(),
//                 createMockPhysicalProgress()
//         );
//
//         when(userService.getPhysicalMeasurementHistory(
//                 eq(userId), eq(Optional.of(startDate)), eq(Optional.of(endDate))))
//                 .thenReturn(mockHistory);
//
//         ResponseEntity<List<PhysicalProgress>> response = userController.getPhysicalMeasurementHistory(
//                 userId, startDate, endDate);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         verify(userService).getPhysicalMeasurementHistory(
//                 eq(userId), eq(Optional.of(startDate)), eq(Optional.of(endDate)));
//     }
//
//     @Test
//     void getLatestPhysicalMeasurement_ShouldReturnLatestMeasurement() {
//         UUID userId = UUID.randomUUID();
//         PhysicalProgress mockProgress = createMockPhysicalProgress();
//
//         when(userService.getLatestPhysicalMeasurement(userId))
//                 .thenReturn(Optional.of(mockProgress));
//
//         ResponseEntity<PhysicalProgress> response = userController.getLatestPhysicalMeasurement(userId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertNotNull(response.getBody());
//         verify(userService).getLatestPhysicalMeasurement(userId);
//     }
//
//     @Test
//     void getLatestPhysicalMeasurement_WhenNoMeasurement_ShouldReturnNotFound() {
//         UUID userId = UUID.randomUUID();
//
//         when(userService.getLatestPhysicalMeasurement(userId))
//                 .thenReturn(Optional.empty());
//
//         ResponseEntity<PhysicalProgress> response = userController.getLatestPhysicalMeasurement(userId);
//
//         assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//         verify(userService).getLatestPhysicalMeasurement(userId);
//     }
//
//     @Test
//     void createGoal_ShouldReturnSuccess() {
//         UUID userId = UUID.randomUUID();
//         List<String> goals = Arrays.asList("Lose weight", "Get stronger");
//
//         doNothing().when(goalService).addUserGoal(userId, goals);
//
//         ResponseEntity<String> response = userController.createGoal(userId, goals);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Goals updated and recommendations refreshed.", response.getBody());
//         verify(goalService).addUserGoal(userId, goals);
//     }
//
//     @Test
//     void getUserGoals_ShouldReturnGoals() {
//         UUID userId = UUID.randomUUID();
//         List<Goal> mockGoals = Arrays.asList(
//                 createMockGoal("Lose weight"),
//                 createMockGoal("Get stronger")
//         );
//
//         when(goalService.getGoalsByUser(userId)).thenReturn(mockGoals);
//
//         ResponseEntity<List<Goal>> response = userController.getUserGoals(userId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         verify(goalService).getGoalsByUser(userId);
//     }
//
//     @Test
//     void getUserRoutines_ShouldReturnRoutines() {
//         UUID userId = UUID.randomUUID();
//         List<Routine> mockRoutines = Arrays.asList(
//                 createMockRoutine("Routine 1"),
//                 createMockRoutine("Routine 2")
//         );
//
//         when(userService.getUserRoutines(userId)).thenReturn(mockRoutines);
//
//         ResponseEntity<List<Routine>> response = userController.getUserRoutines(userId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals(2, response.getBody().size());
//         verify(userService).getUserRoutines(userId);
//     }
//
//     @Test
//     void getCurrentRoutine_ShouldReturnCurrentRoutine() {
//         UUID userId = UUID.randomUUID();
//         Routine mockRoutine = createMockRoutine("Current Routine");
//
//         when(routineRepository.findCurrentRoutineByUserId(userId))
//                 .thenReturn(Optional.of(mockRoutine));
//
//         ResponseEntity<Routine> response = userController.getCurrentRoutine(userId);
//
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertEquals("Current Routine", response.getBody().getName());
//         verify(routineRepository).findCurrentRoutineByUserId(userId);
//     }
//
//     @Test
//     void assignRoutineToUser_ShouldReturnNoContent() {
//         // Arrange
//         UUID userId = UUID.randomUUID();
//         UUID routineId = UUID.randomUUID();
//
//         doNothing().when(userService).assignRoutineToUser(userId, routineId);
//
//         ResponseEntity<Void> response = userController.assignRoutineToUser(userId, routineId);
//
//         assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//         verify(userService).assignRoutineToUser(userId, routineId);
//     }
//
//     private User createMockUser(String name) {
//         User user = new User();
//         user.setId(UUID.randomUUID());
//         user.setName(name);
//         user.setEmail(name.toLowerCase().replace(" ", ".") + "@example.com");
//         return user;
//     }
//
//     private PhysicalProgressDTO createMockPhysicalProgressDTO() {
//         PhysicalProgressDTO dto = new PhysicalProgressDTO();
//
//         WeightDTO weightDTO = new WeightDTO();
//         weightDTO.setValue(70.0);
//         dto.setWeight(weightDTO);
//
//         BodyMeasurementsDTO measurementsDTO = new BodyMeasurementsDTO();
//         measurementsDTO.setHeight(175.0);
//         measurementsDTO.setChestCircumference(95.0);
//         measurementsDTO.setWaistCircumference(80.0);
//         measurementsDTO.setHipCircumference(90.0);
//         measurementsDTO.setBicepsCircumference(35.0);
//         measurementsDTO.setThighCircumference(55.0);
//         dto.setMeasurements(measurementsDTO);
//
//         dto.setPhysicalGoal("Gain muscle");
//         dto.setTrainerObservations("Good progress");
//
//         return dto;
//     }
//
//     private PhysicalProgress createMockPhysicalProgress() {
//         PhysicalProgress progress = new PhysicalProgress();
//         progress.setId(UUID.randomUUID());
//         progress.setWeight(new Weight(70.0, Weight.WeightUnit.KG));
//
//         BodyMeasurements measurements = new BodyMeasurements();
//         measurements.setHeight(175.0);
//         measurements.setChestCircumference(95.0);
//         measurements.setWaistCircumference(80.0);
//         measurements.setHipCircumference(90.0);
//         measurements.setBicepsCircumference(35.0);
//         measurements.setThighCircumference(55.0);
//         progress.setMeasurements(measurements);
//
//         progress.setPhysicalGoal("Gain muscle");
//         progress.setTrainerObservations("Good progress");
//
//         return progress;
//     }
//
//     private Goal createMockGoal(String description) {
//         Goal goal = new Goal();
//         goal.setId(UUID.randomUUID());
//         goal.setDescription(description);
//         goal.setCreationDate(LocalDate.now());
//         return goal;
//     }
//
//     private Routine createMockRoutine(String name) {
//         Routine routine = new Routine();
//         routine.setId(UUID.randomUUID());
//         routine.setName(name);
//         routine.setDescription("Description for " + name);
//         routine.setDifficulty("Intermediate");
//         routine.setGoal("Strength");
//         routine.setCreationDate(LocalDate.now());
//         routine.setExercises(new ArrayList<>());
//         return routine;
//     }
// }