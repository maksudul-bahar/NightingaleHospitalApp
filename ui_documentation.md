# Nightingale Hospital App - UI Component Documentation

This document serves as an index and reference for the custom UI Design System and Component Library implemented for the Nightingale Hospital App. 

All UI components are built using Jetpack Compose and Material Design 3.

---

## Phase 1: Design System (Theme, Colors & Typography)

**Directory:** `app/src/main/java/com/example/nightingalehospitalapp/ui/theme/`

This folder acts as the single source of truth for the app's visual identity. You should never hardcode colors or font sizes in individual screens; always use the definitions provided here.

### Files & Contents:

1.  **`Color.kt`**
    *   **Purpose:** Defines the exact hex codes for the application's color palette for both Light and Dark modes.
    *   **Key Variables:**
        *   `PrimaryBlue`, `PrimaryContainer`: Trustworthy medical blue tones.
        *   `SecondaryTeal`, `SecondaryContainer`: Calming teal accents.
        *   `SuccessGreen`, `WarningYellow`, `ErrorRed`: Semantic status colors.

2.  **`Type.kt`**
    *   **Purpose:** Defines the typography hierarchy and text styles.
    *   **Key Variables:** 
        *   `AppFont`: The base font family (currently `SansSerif`).
        *   `Typography`: A `androidx.compose.material3.Typography` object that explicitly sizes `display`, `headline`, `title`, `body`, and `label` text styles for consistency.

3.  **`Theme.kt`**
    *   **Purpose:** The central theme wrapper that applies the colors and typography to the app.
    *   **Key Components:**
        *   `NightingaleHospitalAppTheme`: The main `@Composable` wrapper. All screens/activities in the app must be wrapped in this composable to inherit the correct styling.

---

## Phase 2: Reusable Component Library

**Directory:** `app/src/main/java/com/example/nightingalehospitalapp/ui/components/`

This folder contains pre-styled, standardized UI blocks. Whenever you are building a new screen, you should import and use these components instead of standard Compose elements (e.g., use `NightingalePrimaryButton` instead of a generic `Button`).

### Files & Contents:

1.  **`NightingaleButtons.kt`**
    *   **Purpose:** Standardized buttons with consistent height (56dp) and rounded corners (12dp).
    *   **Components:**
        *   `NightingalePrimaryButton(text, onClick, ...)`: Solid blue button for primary actions (e.g., Submit, Login).
        *   `NightingaleSecondaryButton(text, onClick, ...)`: Outlined button for alternative actions (e.g., Cancel).
        *   `NightingaleTextButton(text, onClick, ...)`: Plain text button for subtle actions.

2.  **`NightingaleTextFields.kt`**
    *   **Purpose:** Smart, robust text inputs.
    *   **Components:**
        *   `NightingaleTextField(value, onValueChange, label, isError, errorMessage, ...)`: An outlined text field that automatically handles standard styling, rounded corners (12dp), and built-in error message displays.

3.  **`NightingaleCards.kt`**
    *   **Purpose:** Consistent structural containers for grouping information (like a Patient Profile or a Test Result). Both variants enforce a standard `16.dp` internal padding.
    *   **Components:**
        *   `NightingaleElevatedCard(content)`: A card with a 4dp shadow drop for depth.
        *   `NightingaleOutlinedCard(content)`: A flat card with a subtle border outline.

---

## Phase 3: Layouts & Navigation

**Directory:** `app/src/main/java/com/example/nightingalehospitalapp/ui/components/`

This folder also contains scalable structural layout wrappers for the application to handle consistent routing and standard toolbars.

### Files & Contents:

1.  **`NightingaleNavLayouts.kt`**
    *   **Purpose:** Reusable Scaffolds for Role-Based Navigation across multiple activities.
    *   **Components:**
        *   `NightingaleAdminScaffold(title, context, currentActivityClass, content)`: Automatically wraps the screen in a `ModalNavigationDrawer` containing all the admin links (Manage Doctors, Surgeries, etc.) and provides a TopAppBar with a menu icon to open the drawer.
        *   `NightingaleUserScaffold(title, currentTab, onTabSelected, showBottomBar, content)`: Automatically wraps the screen in a standard `Scaffold` with a Bottom `NavigationBar` for standard users (Patients/Doctors) to easily switch between Home, Appointments, and Profile.

---

## Phase 4: Polish & Micro-interactions

**Directory:** `app/src/main/java/com/example/nightingalehospitalapp/ui/components/`

This folder contains components that add 'Wow' factor elements such as loading placeholders and empty states.

### Files & Contents:

1.  **`NightingaleShimmer.kt`**
    *   **Purpose:** Animated loading placeholders.
    *   **Components:**
        *   `Modifier.shimmerEffect()`: An extension that applies a sweeping animated gradient to simulate loading.
        *   `DoctorCardShimmer()`: A layout mimicking a Doctor Profile Card to be shown while doctor data fetches.
        *   `NightingaleListShimmer()`: A generic layout mimicking a standard list card to be shown while list data fetches (used across admin screens).

2.  **`NightingaleEmptyState.kt`**
    *   **Purpose:** Beautiful centered layouts for empty lists.
    *   **Components:**
        *   `NightingaleEmptyState(title, message, icon)`: A standardized empty state with an illustrative icon and helpful text, ensuring users never stare at a blank screen.
