import React, { ComponentType } from "react";
import {
    NavigateFunction,
    Params,
    useLocation,
    useNavigate,
    useParams,
    Location,
} from "react-router-dom";

// as seen in
// https://github.com/remix-run/react-router/issues/8146#issuecomment-1109534957

interface RouterProps {
    navigate: NavigateFunction;
    readonly params: Params<string>;
    location: Location;
}

export type WithRouterProps<T> = T & RouterProps;
type OmitRouter<T> = Omit<T, keyof RouterProps>;

export function withRouter<T>(
    Component: ComponentType<OmitRouter<T> & RouterProps>
) {
    return (props: OmitRouter<T>) => {
        const location = useLocation();
        const navigate = useNavigate();
        const params = useParams();
        return (
            <Component
                location={location}
                navigate={navigate}
                params={params}
                {...props}
            />
        );
    };
}