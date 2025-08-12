import type { State } from '../reducers/app';

type Props = Pick<State, 'anime' | 'timezone'>;

// TODO add days and anime components
function Schedule({ anime }: Props) {
    return (
        <section className="column centered">
            <h2>Schedule</h2>
            <ul>
                {anime?.map((a) => (
                    <li key={a.id}>{a.romajiTitle}</li>
                ))}
            </ul>
        </section>
    );
}

export default Schedule;
