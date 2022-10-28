import {createSlice, PayloadAction} from "@reduxjs/toolkit";

interface UISlice {
    language: string
}

export const themeSlice = createSlice({
    name: 'ui-slice',
    initialState: {
        language: "default",

        profileBtnMenuOpen: false
    } as UISlice,
    reducers: {
        setLanguage(state, action: PayloadAction<string>) {
            state.language = action.payload
        }
    }
})

export const {
    setLanguage
} = themeSlice.actions

export default themeSlice.reducer